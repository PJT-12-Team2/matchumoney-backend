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
import java.util.*;

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
        log.info("KB카드 정보 및 거래내역 동기화 시작 (참고 프로젝트 방식) - 사용자: {}", userId);
        
        // 1. KB카드 정보와 각 카드의 거래내역을 함께 조회 (참고 프로젝트 방식)
        List<CardHoldingVO> cards = kbCardApiUtil.fetchKbCardsWithTransactions(kbId, kbPw, userId);

        log.info("API 조회 완료 - 카드: {}개", cards.size());

        // 2. 기존 카드 데이터 삭제
        kbCardMapper.deleteKbCardById(userId);
        
        // 3. 카드 정보 저장 및 매칭
        for (CardHoldingVO card : cards) {
            // MyData에서 가져온 카드명으로 카드고릴라 카드 매칭 시도
            log.info("🔍 카드고릴라 매칭 시도: '{}' (finId: {}, holdingId: {})", 
                card.getCardName(), card.getFinId(), card.getHoldingId());
            Optional<CardVO> matchedCard = matchCardGorillaCard(card.getCardName());
            if (matchedCard.isPresent()) {
                card.setCardId(matchedCard.get().getCardId());
                log.info("✅ 카드고릴라 매칭 성공: '{}' -> card_id2: {}", 
                    card.getCardName(), matchedCard.get().getCardId());
            } else {
                log.warn("⚠️ 카드고릴라에서 카드명 '{}'에 대한 매칭된 카드를 찾을 수 없습니다.", card.getCardName());
                card.setCardId(null);
            }
            kbCardMapper.insertKbCard(card);
        }
        
        // 4. 카드 저장 후 자동으로 매칭되지 않은 카드들에 대해 매칭 시도
        try {
            cardMatchingService.matchCardHoldingsByUserId(userId);
            log.info("자동 카드 매칭 완료");
        } catch (Exception e) {
            log.error("자동 카드 매칭 실패: {}", e.getMessage(), e);
        }
        
        // 5. 각 카드의 거래내역 처리 (중복 방지)
        int totalNewTransactions = 0;
        int totalDuplicateSkipped = 0;
        
        for (CardHoldingVO card : cards) {
            if (card.getTransactions() != null && !card.getTransactions().isEmpty()) {
                log.info("카드 '{}' 거래내역 {}건 중복 체크 후 저장 시작", 
                    card.getCardName(), card.getTransactions().size());
                
                int newCount = 0;
                int duplicateCount = 0;
                
                for (CardTransactionVO transaction : card.getTransactions()) {
                    // 중복 체크
                    boolean exists = kbCardTransactionMapper.existsTransaction(
                            userId, 
                            transaction.getResCardNo(),
                            transaction.getResUsedDate(),
                            transaction.getResUsedTime(),
                            transaction.getResApprovalNo()
                    );
                    
                    if (exists) {
                        duplicateCount++;
                        continue;
                    }
                    
                    // DB에 저장할 때 필요한 필드 설정
                    transaction.setUserId(userId);
                    transaction.setFinId(card.getFinId());
                    transaction.setHoldingId(card.getHoldingId()); // holdingId 설정 추가
                    transaction.setCardId2(card.getCardId()); // 카드고릴라 매칭 ID
                    transaction.setCardName(card.getCardName());
                    
                    // 로깅 추가 - 카드 매칭 상태 확인
                    log.info("🔍 거래내역 저장 - 카드: '{}', cardId2: {}, finId: {}, holdingId: {}", 
                        card.getCardName(), card.getCardId(), card.getFinId(), card.getHoldingId());
                    
                    // 가맹점명을 기반으로 소비 분야 자동 분류
                    String originalMerchantName = transaction.getResMemberStoreName();
                    if (originalMerchantName != null && !originalMerchantName.isEmpty()) {
                        String category = merchantCategoryService.categorizeByMerchantName(originalMerchantName);
                        transaction.setResMemberStoreType(category);
                    }
                    
                    try {
                        kbCardTransactionMapper.insertKbCardTransaction(transaction);
                        newCount++;
                    } catch (Exception e) {
                        log.error("거래 내역 저장 실패 - 날짜: {}, 가맹점: '{}', 금액: {}, 에러: {}", 
                            transaction.getResUsedDate(), originalMerchantName, 
                            transaction.getResUsedAmount(), e.getMessage());
                    }
                }
                
                totalNewTransactions += newCount;
                totalDuplicateSkipped += duplicateCount;
                
                log.info("카드 '{}' 거래내역 처리 완료: 신규 {}건, 중복 스킵 {}건", 
                    card.getCardName(), newCount, duplicateCount);
                
                // 추천 재계산 트리거 (카드고릴라 매칭이 된 카드만)
                if (card.getCardId() != null && newCount > 0) {
                    try {
                        log.info("사용자 {}의 카드 {} 추천 재계산 트리거", userId, card.getCardId());
                        cardRecommendationRefreshService.refreshRecommendationsForUserCard(userId, card.getCardId());
                    } catch (Exception e) {
                        log.warn("추천 재계산 트리거 중 오류 발생: 사용자 {}, 카드 {}", userId, card.getCardId(), e);
                    }
                }
            } else {
                log.debug("카드 '{}'에 대한 거래내역이 없습니다.", card.getCardName());
            }
        }
        
        log.info("KB카드 동기화 완료 - 사용자 {}: 카드 {}개, 신규 거래내역 {}건 추가, 중복 {}건 스킵", 
            userId, cards.size(), totalNewTransactions, totalDuplicateSkipped);
        
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
        
        log.info("🔄 개별 카드 업데이트 요청을 전체 카드 업데이트로 변경 - 사용자: {}, holdingId: {}", userId, holdingId);
        
        // connectedId 가져오기 (이미 DB에 저장된 cardInfo를 통해)
        List<CardHoldingVO> userCards = kbCardMapper.selectKbCardByUserId(userId);
        if (userCards.isEmpty()) {
            throw new RuntimeException("사용자의 카드 정보가 없습니다. 먼저 카드를 등록해주세요.");
        }
        
        String connectedId = userCards.get(0).getConnectedId();
        if (connectedId == null || connectedId.isEmpty()) {
            throw new RuntimeException("connectedId가 유효하지 않습니다. 카드 연동이 필요합니다.");
        }

        log.info("🔄 connectedId 기반 전체 거래내역 업데이트 시작 - 기간: {} ~ {}", startDate, endDate);
        
        // connectedId로 모든 카드의 거래내역을 업데이트 (카드번호 불필요)
        List<CardTransactionVO> allTransactions = syncTransactionsByConnectedIdWithDateRange(userId, connectedId, startDate, endDate);
        
        // 요청된 holdingId에 해당하는 거래내역만 필터링해서 반환
        List<CardTransactionVO> filteredTransactions = allTransactions.stream()
                .filter(transaction -> {
                    // holdingId가 일치하는 거래내역만 반환
                    return transaction.getHoldingId() != null && transaction.getHoldingId().equals(holdingId);
                })
                .collect(java.util.stream.Collectors.toList());
                
        log.info("✅ 전체 {}건 중 요청 카드 거래내역 {}건 반환", allTransactions.size(), filteredTransactions.size());
        
        return filteredTransactions;
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

    @Override
    public List<CardTransactionVO> syncTransactionsByConnectedId(Long userId, String connectedId) throws Exception {
        // 최근 30일 거래내역 조회
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        log.info("connectedId를 이용한 거래내역 동기화 시작 - 사용자: {}, 기간: {} ~ {}", userId, startDate, endDate);
        
        // connectedId로 모든 카드의 거래내역 조회
        List<CardTransactionVO> allTransactions = kbCardApiUtil.fetchKbCardTransactionsByConnectedId(
                connectedId, startDate, endDate, userId);
        
        if (allTransactions.isEmpty()) {
            log.info("조회된 거래내역이 없습니다.");
            return allTransactions;
        }
        
        // 사용자의 기존 카드 정보 조회 (카드번호 매칭용)
        List<CardHoldingVO> userCards = kbCardMapper.selectKbCardByUserId(userId);
        Map<String, CardHoldingVO> cardMap = userCards.stream()
                .collect(java.util.stream.Collectors.toMap(CardHoldingVO::getResCardNo, card -> card));
        
        List<CardTransactionVO> savedTransactions = new ArrayList<>();
        int duplicateCount = 0;
        
        for (CardTransactionVO transaction : allTransactions) {
            // 중복 체크
            boolean exists = kbCardTransactionMapper.existsTransaction(
                    userId, 
                    transaction.getResCardNo(),
                    transaction.getResUsedDate(),
                    transaction.getResUsedTime(),
                    transaction.getResApprovalNo()
            );
            
            if (exists) {
                duplicateCount++;
                log.debug("중복 거래내역 건너뜀: {} - {} {}", 
                        transaction.getResMemberStoreName(), 
                        transaction.getResUsedDate(), 
                        transaction.getResUsedTime());
                continue;
            }
            
            // 카드번호로 기존 카드 정보와 매칭
            CardHoldingVO matchedCard = cardMap.get(transaction.getResCardNo());
            if (matchedCard != null) {
                transaction.setFinId(matchedCard.getFinId());
                transaction.setHoldingId(matchedCard.getHoldingId()); // holdingId 설정 추가
                transaction.setCardId2(matchedCard.getCardId());
                transaction.setCardName(matchedCard.getCardName());
            } else {
                log.warn("매칭되지 않은 카드번호: {} - 거래내역 저장하지만 카드 정보 누락", transaction.getResCardNo());
                // finId, holdingId, cardId2를 null로 설정하고 저장
                transaction.setFinId(null);
                transaction.setHoldingId(null);
                transaction.setCardId2(null);
            }
            
            // 가맹점명 기반 카테고리 자동 분류
            String category = merchantCategoryService.categorizeByMerchantName(transaction.getResMemberStoreName());
            transaction.setResMemberStoreType(category);
            
            try {
                kbCardTransactionMapper.insertKbCardTransaction(transaction);
                savedTransactions.add(transaction);
                
                log.debug("거래내역 저장 완료: {} - {} {} ({})", 
                        transaction.getResMemberStoreName(),
                        transaction.getResUsedDate(),
                        transaction.getResUsedTime(),
                        category);
            } catch (Exception e) {
                log.error("거래내역 저장 실패: {} - {}", transaction.getResMemberStoreName(), e.getMessage());
            }
        }
        
        log.info("거래내역 동기화 완료 - 총 조회: {}건, 중복: {}건, 저장: {}건", 
                allTransactions.size(), duplicateCount, savedTransactions.size());
        
        // 추천 재계산 트리거 (저장된 거래내역이 있는 카드들에 대해)
        Set<Integer> affectedCardIds = savedTransactions.stream()
                .filter(t -> t.getCardId2() != null)
                .map(CardTransactionVO::getCardId2)
                .collect(java.util.stream.Collectors.toSet());
        
        for (Integer cardId : affectedCardIds) {
            try {
                log.info("카드 {}에 대한 추천 재계산 트리거", cardId);
                cardRecommendationRefreshService.refreshRecommendationsForUserCard(userId, cardId);
            } catch (Exception e) {
                log.warn("추천 재계산 트리거 중 오류 발생: 사용자 {}, 카드 {} - {}", userId, cardId, e.getMessage());
            }
        }
        
        return savedTransactions;
    }

    /**
     * connectedId를 이용하여 특정 날짜 범위의 거래내역을 동기화하는 메서드
     * @param userId 사용자 ID
     * @param connectedId 연결 ID  
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 동기화된 거래내역 목록
     * @throws Exception API 호출 실패시
     */
    public List<CardTransactionVO> syncTransactionsByConnectedIdWithDateRange(
            Long userId, String connectedId, LocalDate startDate, LocalDate endDate) throws Exception {
        
        log.info("connectedId를 이용한 거래내역 동기화 시작 - 사용자: {}, 기간: {} ~ {}", userId, startDate, endDate);
        
        // connectedId로 모든 카드의 거래내역 조회
        List<CardTransactionVO> allTransactions = kbCardApiUtil.fetchKbCardTransactionsByConnectedId(
                connectedId, startDate, endDate, userId);
        
        if (allTransactions.isEmpty()) {
            log.info("조회된 거래내역이 없습니다.");
            return allTransactions;
        }
        
        // 사용자의 기존 카드 정보 조회 (카드번호 매칭용)
        List<CardHoldingVO> userCards = kbCardMapper.selectKbCardByUserId(userId);
        Map<String, CardHoldingVO> cardMap = userCards.stream()
                .collect(java.util.stream.Collectors.toMap(CardHoldingVO::getResCardNo, card -> card));
        
        List<CardTransactionVO> savedTransactions = new ArrayList<>();
        int duplicateCount = 0;
        
        for (CardTransactionVO transaction : allTransactions) {
            // 중복 체크
            boolean exists = kbCardTransactionMapper.existsTransaction(
                    userId, 
                    transaction.getResCardNo(),
                    transaction.getResUsedDate(),
                    transaction.getResUsedTime(),
                    transaction.getResApprovalNo()
            );
            
            if (exists) {
                duplicateCount++;
                log.debug("중복 거래내역 건너뜀: {} - {} {}", 
                        transaction.getResMemberStoreName(), 
                        transaction.getResUsedDate(), 
                        transaction.getResUsedTime());
                continue;
            }
            
            // 카드번호로 기존 카드 정보와 매칭
            CardHoldingVO matchedCard = cardMap.get(transaction.getResCardNo());
            if (matchedCard != null) {
                transaction.setFinId(matchedCard.getFinId());
                transaction.setHoldingId(matchedCard.getHoldingId()); // holdingId 설정 추가
                transaction.setCardId2(matchedCard.getCardId());
                transaction.setCardName(matchedCard.getCardName());
            } else {
                log.warn("매칭되지 않은 카드번호: {} - 거래내역 저장하지만 카드 정보 누락", transaction.getResCardNo());
                // finId, holdingId, cardId2를 null로 설정하고 저장
                transaction.setFinId(null);
                transaction.setHoldingId(null);
                transaction.setCardId2(null);
            }
            
            // 가맹점명 기반 카테고리 자동 분류
            String category = merchantCategoryService.categorizeByMerchantName(transaction.getResMemberStoreName());
            transaction.setResMemberStoreType(category);
            
            try {
                kbCardTransactionMapper.insertKbCardTransaction(transaction);
                savedTransactions.add(transaction);
                
                log.debug("거래내역 저장 완료: {} - {} {} ({})", 
                        transaction.getResMemberStoreName(),
                        transaction.getResUsedDate(),
                        transaction.getResUsedTime(),
                        category);
            } catch (Exception e) {
                log.error("거래내역 저장 실패: {} - {}", transaction.getResMemberStoreName(), e.getMessage());
            }
        }
        
        log.info("거래내역 동기화 완료 - 총 조회: {}건, 중복: {}건, 저장: {}건", 
                allTransactions.size(), duplicateCount, savedTransactions.size());
        
        // 추천 재계산 트리거 (저장된 거래내역이 있는 카드들에 대해)
        Set<Integer> affectedCardIds = savedTransactions.stream()
                .filter(t -> t.getCardId2() != null)
                .map(CardTransactionVO::getCardId2)
                .collect(java.util.stream.Collectors.toSet());
        
        for (Integer cardId : affectedCardIds) {
            try {
                log.info("카드 {}에 대한 추천 재계산 트리거", cardId);
                cardRecommendationRefreshService.refreshRecommendationsForUserCard(userId, cardId);
            } catch (Exception e) {
                log.warn("추천 재계산 트리거 중 오류 발생: 사용자 {}, 카드 {} - {}", userId, cardId, e.getMessage());
            }
        }
        
        return savedTransactions;
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