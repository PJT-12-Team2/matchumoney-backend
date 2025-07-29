package team2.pjt12.matchumoney.domain.mydata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.mydata.mapper.CardMatchingMapper;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.dto.res.CardMatchingResultDTO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardVO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardMatchingServiceImpl implements CardMatchingService {
    
    private final CardMatchingMapper cardMatchingMapper;
    
    @Override
    @Transactional
    public CardMatchingResultDTO matchAllCardHoldings() {
        try {
            // 매칭되지 않은 카드 수 확인
            List<CardHoldingVO> unmatchedCards = cardMatchingMapper.findUnmatchedCardHoldings();
            int totalCount = unmatchedCards.size();
            
            if (totalCount == 0) {
                return CardMatchingResultDTO.success(0, 0);
            }
            
            // 일괄 매칭 수행
            int updatedCount = cardMatchingMapper.updateAllCardMatching();
            
            log.info("카드 매칭 완료 - 전체: {}건, 매칭 성공: {}건", totalCount, updatedCount);
            
            return CardMatchingResultDTO.success(totalCount, updatedCount);
            
        } catch (Exception e) {
            log.error("카드 매칭 중 오류 발생", e);
            return CardMatchingResultDTO.fail("카드 매칭 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public CardMatchingResultDTO matchCardHoldingsByUserId(Long userId) {
        try {
            // 먼저 해당 사용자의 모든 카드 보유 정보를 확인
            List<CardHoldingVO> allUserCards = cardMatchingMapper.findAllCardHoldingsByUserId(userId);
            log.info("사용자 {}의 전체 카드 보유 정보: {} 건", userId, allUserCards.size());
            
            // 그 중 매칭되지 않은 카드 수 계산
            int userUnmatchedCount = (int) allUserCards.stream()
                    .filter(card -> card.getCardId() == null)
                    .count();
                    
            log.info("사용자 {}의 매칭되지 않은 카드: {} 건", userId, userUnmatchedCount);
            
            // 매칭되지 않은 카드들 상세 로그
            allUserCards.stream()
                    .filter(card -> card.getCardId() == null)
                    .forEach(card -> log.info("매칭되지 않은 카드: holdingId={}, cardName='{}'", 
                            card.getHoldingId(), card.getCardName()));
            
            if (userUnmatchedCount == 0) {
                log.info("사용자 {}의 매칭되지 않은 카드가 없습니다.", userId);
                return CardMatchingResultDTO.success(0, 0);
            }
            
            // 사용자별 매칭 수행
            int updatedCount = cardMatchingMapper.updateCardMatchingByUserId(userId);
            
            log.info("사용자 {}의 카드 매칭 완료 - 전체: {}건, 매칭 성공: {}건", 
                    userId, userUnmatchedCount, updatedCount);
            
            return CardMatchingResultDTO.success(userUnmatchedCount, updatedCount);
            
        } catch (Exception e) {
            log.error("사용자 {}의 카드 매칭 중 오류 발생", userId, e);
            return CardMatchingResultDTO.fail("카드 매칭 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    @Override
    public List<CardHoldingVO> getUnmatchedCardHoldings() {
        return cardMatchingMapper.findUnmatchedCardHoldings();
    }
    
    @Override
    @Transactional
    public boolean manualMatchCardHolding(Long holdingId, Integer cardId) {
        try {
            int updatedCount = cardMatchingMapper.updateCardIdForHolding(holdingId, cardId);
            
            if (updatedCount > 0) {
                log.info("수동 카드 매칭 성공 - holdingId: {}, cardId: {}", holdingId, cardId);
                return true;
            } else {
                log.warn("수동 카드 매칭 실패 - holdingId: {}, cardId: {}", holdingId, cardId);
                return false;
            }
            
        } catch (Exception e) {
            log.error("수동 카드 매칭 중 오류 발생 - holdingId: {}, cardId: {}", holdingId, cardId, e);
            return false;
        }
    }
    
    @Override
    public Optional<CardVO> testCardMatching(String cardName) {
        return cardMatchingMapper.testCardMatching(cardName);
    }
    
    @Override
    public List<CardHoldingVO> getAllCardHoldingsByUserId(Long userId) {
        return cardMatchingMapper.findAllCardHoldingsByUserId(userId);
    }
}
