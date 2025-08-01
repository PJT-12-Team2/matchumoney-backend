package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CardBenefitDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.mapper.UserCardRecommendationMapper;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.UserCardRecommendationVO;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCardRecommendationServiceImpl implements UserCardRecommendationService {

    private final UserCardRecommendationMapper userCardRecommendationMapper;

    @Override
    @Transactional
    public void saveRecommendations(Long userId, Integer baseCardId, List<CardBenefitDTO> recommendedCards) {
        log.info("사용자 {}의 추천 카드 {} 개 저장 시작", userId, recommendedCards.size());

        try {
            // 1. 기존 추천 데이터 삭제 (사용자 전체)
            userCardRecommendationMapper.deleteRecommendationsByUserId(userId);
            log.debug("기존 추천 데이터 삭제 완료");

            // 2. 새로운 추천 데이터 저장
            List<UserCardRecommendationVO> recommendations = recommendedCards.stream()
                .map(card -> UserCardRecommendationVO.builder()
                    .userId(userId)
                    .cardId(card.getCardId())
                    .cardName(card.getCardName())
                    .cardType(card.getCardType())
                    .issuer(card.getIssuer())
                    .estimatedBenefit(card.getEstimatedBenefit())
                    .annualFee(card.getAnnualFee())
                    .preMonthMoney(card.getPreMonthMoney())
                    .cardImageUrl(card.getCardImageUrl())
                    .requestPcUrl(card.getRequestPcUrl())
                    .requestMobileUrl(card.getRequestMobileUrl())
                    .build())
                .collect(Collectors.toList());

            // 배치 삽입
            if (!recommendations.isEmpty()) {
                userCardRecommendationMapper.insertRecommendationsBatch(recommendations);
            }

            log.info("사용자 {}의 추천 카드 저장 완료", userId);

        } catch (Exception e) {
            log.error("추천 카드 저장 중 오류 발생: 사용자 {}", userId, e);
            throw new RuntimeException("추천 카드 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<CardBenefitDTO> getSavedRecommendations(Long userId, Integer baseCardId) {
        // 새 테이블 구조에서는 baseCardId 개념이 없으므로 모든 추천을 조회
        return getAllSavedRecommendations(userId).stream()
            .map(this::convertToCardBenefitDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserCardRecommendationVO> getAllSavedRecommendations(Long userId) {
        log.info("사용자 {}의 모든 저장된 추천 카드 조회", userId);

        try {
            List<UserCardRecommendationVO> allRecommendations = 
                userCardRecommendationMapper.selectRecommendationsByUserId(userId);

            log.info("사용자 {}의 모든 저장된 추천 카드 {} 개 조회 완료", userId, allRecommendations.size());
            return allRecommendations;

        } catch (Exception e) {
            log.error("모든 저장된 추천 카드 조회 중 오류 발생: 사용자 {}", userId, e);
            throw new RuntimeException("모든 저장된 추천 카드 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    @Transactional
    public void deleteRecommendations(Long userId, Integer cardId) {
        log.info("사용자 {}의 카드 {} 관련 추천 카드 삭제", userId, cardId);

        try {
            userCardRecommendationMapper.deleteRecommendationsByUserIdAndCardId(userId, cardId);
            log.info("사용자 {}의 카드 {} 관련 추천 카드 삭제 완료", userId, cardId);

        } catch (Exception e) {
            log.error("추천 카드 삭제 중 오류 발생: 사용자 {}, 카드 {}", userId, cardId, e);
            throw new RuntimeException("추천 카드 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * UserCardRecommendationVO를 CardBenefitDTO로 변환합니다.
     */
    private CardBenefitDTO convertToCardBenefitDTO(UserCardRecommendationVO vo) {
        return CardBenefitDTO.builder()
            .cardId(vo.getCardId())
            .cardName(vo.getCardName())
            .cardType(vo.getCardType())
            .issuer(vo.getIssuer())
            .estimatedBenefit(vo.getEstimatedBenefit())
            .annualFee(vo.getAnnualFee())
            .preMonthMoney(vo.getPreMonthMoney())
            .cardImageUrl(vo.getCardImageUrl())
            .requestPcUrl(vo.getRequestPcUrl())
            .requestMobileUrl(vo.getRequestMobileUrl())
            .build();
    }
}