package team2.pjt12.matchumoney.domain.mydata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.mydata.mapper.CardMatchingMapper;
import team2.pjt12.matchumoney.domain.mydata.vo.CardVO; // CardVO 임포트
import team2.pjt12.matchumoney.domain.mydata.mapper.KbCardMapper;
import team2.pjt12.matchumoney.domain.mydata.mapper.KbCardTransactionMapper;
import team2.pjt12.matchumoney.domain.mydata.util.KBCardApiUtil;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardTransactionVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.CardRecommendationRefreshService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KbCardServiceImpl implements KbCardService {

    private final KbCardMapper kbCardMapper;
    private final KbCardTransactionMapper kbCardTransactionMapper;
    private final CardMatchingMapper cardMatchingMapper;
    private final CardMatchingService cardMatchingService;
    private final MerchantCategoryService merchantCategoryService;
    private final KBCardApiUtil kbCardApiUtil;
    private final CardRecommendationRefreshService cardRecommendationRefreshService;


    @Override
    public List<CardHoldingVO> syncAndSaveCards(Long userId, String kbId, String kbPw) throws Exception {
        List<CardHoldingVO> cards = kbCardApiUtil.fetchKbCards(kbId, kbPw, userId);

        kbCardMapper.deleteKbCardById(userId);
        for (CardHoldingVO card : cards) {
            // MyData에서 가져온 카드명으로 카드고릴라 카드 매칭 시도
            Optional<CardVO> matchedCard = matchCardGorillaCard(card.getCardName());
            if (matchedCard.isPresent()) {
                // 매칭 성공 시, CardHoldingVO의 cardId 필드를 카드고릴라 카드의 card_id로 업데이트
                card.setCardId(matchedCard.get().getCardId());
            } else {
                // 매칭 실패 시, null로 설정 (Foreign Key 제약조건을 피하기 위해)
                System.out.println("⚠️ 카드고릴라에서 카드명 '" + card.getCardName() + "' 에 대한 매칭된 카드를 찾을 수 없습니다.");
                card.setCardId(null); // 매칭 실패한 카드는 null로 설정
            }
            kbCardMapper.insertKbCard(card);
        }
        
        // 카드 저장 후 자동으로 매칭되지 않은 카드들에 대해 매칭 시도
        try {
            cardMatchingService.matchCardHoldingsByUserId(userId);
            log.info("자동 카드 매칭 완료");
        } catch (Exception e) {
            log.error("자동 카드 매칭 실패: {}", e.getMessage(), e);
        }
        
        return cards;
    }

    @Override
    public List<CardHoldingVO> getCards(Long userId) {
        return kbCardMapper.selectKbCardByUserId(userId);
    }

    @Override
    public List<CardTransactionVO> syncAndSaveCardTransactions(
            Long userId, Long holdingId, String cardNo, String cardPw2, String birthDate, LocalDate startDate, LocalDate endDate
    ) throws Exception {
        // connectedId 가져오기 (이미 DB에 저장된 cardInfo를 통해)
        CardHoldingVO cardInfo = kbCardMapper.selectKbCardByUserId(userId)
                .stream()
                .filter(card -> card.getHoldingId() != null && card.getHoldingId().equals(holdingId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 holdingId를 가진 카드를 찾을 수 없습니다."));

        String connectedId = cardInfo.getConnectedId();
        if (connectedId == null || connectedId.isEmpty()) {
            throw new RuntimeException("connectedId가 유효하지 않습니다. 카드 연동이 필요합니다.");
        }

        // CodeF API를 통해 거래 내역 조회
        List<CardTransactionVO> transactions = kbCardApiUtil.fetchKbCardTransactions(
                connectedId, cardNo, cardPw2, birthDate, startDate, endDate, userId,
                cardInfo.getFinId(), // CardHoldingVO의 finId를 CardTransactionVO의 finId로 사용
                cardInfo.getCardId(), // CardHoldingVO의 cardId (카드고릴라 idx)를 CardTransactionVO의 cardId2로 사용
                cardInfo.getCardName() // CardHoldingVO의 cardName을 CardTransactionVO의 cardName으로 사용
        );

        // 기존 거래 내역 삭제 및 새 거래 내역 저장
        log.debug("기존 거래 내역 삭제 시작 - holdingId: {}", holdingId);
        kbCardTransactionMapper.deleteKbCardTransactionsByHoldingId(holdingId);
        
        log.debug("새로운 거래 내역 저장 시작 - 총 {}건", transactions.size());
        for (int i = 0; i < transactions.size(); i++) {
            CardTransactionVO transaction = transactions.get(i);
            
            // 가맹점명을 기반으로 소비 분야 자동 분류
            String originalMerchantName = transaction.getResMemberStoreName();
            String category = merchantCategoryService.categorizeByMerchantName(originalMerchantName);
            transaction.setResMemberStoreType(category);
            
            log.debug("거래 내역 분류 [{}/{}]: '{}' -> '{}'", 
                    i + 1, transactions.size(), originalMerchantName, category);
            
            try {
                kbCardTransactionMapper.insertKbCardTransaction(transaction);
            } catch (Exception e) {
                log.error("거래 내역 저장 실패 - 가맹점: '{}', 에러: {}", originalMerchantName, e.getMessage());
                throw new RuntimeException("거래 내역 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
            }
        }
        
        log.info("거래 내역 저장 완료 - 총 {}건 저장됨", transactions.size());
        
        // 거래내역 업데이트 후 추천 카드 재계산 트리거 (비동기)
        try {
            if (cardInfo.getCardId() != null) { // 카드고릴라 매칭이 된 카드만
                log.info("사용자 {}의 카드 {} 추천 재계산 트리거", userId, cardInfo.getCardId());
                cardRecommendationRefreshService.refreshRecommendationsForUserCard(userId, cardInfo.getCardId());
            } else {
                log.debug("카드고릴라 매칭이 되지 않은 카드로 추천 재계산을 건너뜁니다. 카드명: {}", cardInfo.getCardName());
            }
        } catch (Exception e) {
            log.warn("추천 재계산 트리거 중 오류 발생하였으나 거래내역 저장은 성공: 사용자 {}, 카드 {}", 
                userId, cardInfo.getCardId(), e);
        }
        
        return transactions;
    }

    @Override
    public List<CardTransactionVO> getCardTransactions(Long userId, Long holdingId) {
        return kbCardTransactionMapper.selectKbCardTransactionsByUserIdAndHoldingId(userId, holdingId);
    }

    @Override
    public Map<String, Long> getCategoryStatistics(List<CardTransactionVO> transactions) {
        return transactions.stream()
                .filter(t -> t.getResMemberStoreType() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        CardTransactionVO::getResMemberStoreType,
                        java.util.stream.Collectors.counting()));
    }

    /**
     * MyData로 가져온 카드명에 기반하여 카드고릴라의 카드 정보를 매칭하는 메서드.
     * @param mydataCardName MyData API를 통해 조회된 카드명
     * @return 매칭된 CardVO (없으면 Optional.empty())
     */
    public Optional<CardVO> matchCardGorillaCard(String mydataCardName) {
        return cardMatchingMapper.findMatchingCardByName(mydataCardName);
    }
}