package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CardRecommendationResponseDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.mapper.CardRecommendationMapper;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardProductVO;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardRecommendationRefreshServiceImpl implements CardRecommendationRefreshService {

    private final CardRecommendationService cardRecommendationService;
    private final CardRecommendationMapper cardRecommendationMapper;

    @Override
    @Async("cardRecommendationExecutor")
    public void refreshRecommendationsForUser(Long userId) {
        log.info("사용자 {}의 모든 보유 카드에 대한 추천 재계산 시작", userId);

        try {
            // 사용자가 보유한 모든 카드 조회
            List<CardProductVO> ownedCards = cardRecommendationMapper.selectUserOwnedCards(userId);
            
            if (ownedCards.isEmpty()) {
                log.info("사용자 {}의 보유 카드가 없어 추천 재계산을 건너뜁니다.", userId);
                return;
            }

            // 각 보유 카드에 대해 추천 재계산
            for (CardProductVO card : ownedCards) {
                try {
                    refreshRecommendationsForUserCard(userId, card.getCardProductId());
                } catch (Exception e) {
                    log.warn("사용자 {}의 카드 {} 추천 재계산 중 오류 발생, 다음 카드로 진행", 
                        userId, card.getCardProductId(), e);
                }
            }

            log.info("사용자 {}의 모든 보유 카드 추천 재계산 완료. 총 {} 개 카드 처리", userId, ownedCards.size());

        } catch (Exception e) {
            log.error("사용자 {}의 추천 재계산 중 전체 오류 발생", userId, e);
        }
    }

    @Override
    @Async("cardRecommendationExecutor")
    public void refreshRecommendationsForUserCard(Long userId, Integer cardId) {
        log.info("사용자 {}의 카드 {} 추천 재계산 시작", userId, cardId);

        try {
            // 추천 카드 자동 생성 및 저장
            cardRecommendationService.generateAndSaveRecommendations(userId, cardId);
            
            log.info("사용자 {}의 카드 {} 추천 재계산 완료", userId, cardId);

        } catch (Exception e) {
            log.error("사용자 {}의 카드 {} 추천 재계산 중 오류 발생", userId, cardId, e);
        }
    }
}