package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CardBenefitDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.mapper.UserCardRecommendationMapper;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.UserCardRecommendationVO;
import team2.pjt12.matchumoney.domain.carddetail.mapper.CardDetailMapper;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCardRecommendationServiceImpl implements UserCardRecommendationService {

    private final UserCardRecommendationMapper userCardRecommendationMapper;
    private final CardDetailMapper cardDetailMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void saveRecommendations(Long userId, Integer baseCardId, List<CardBenefitDTO> recommendedCards) {
        log.info("사용자 {}의 기준 카드 {}에 대한 추천 카드 {} 개 저장 시작", userId, baseCardId, recommendedCards.size());

        try {
            // 1. 기존 추천 데이터 삭제 (특정 기준 카드에 대한 것만)
            userCardRecommendationMapper.deleteRecommendationsByUserIdAndBaseCardId(userId, baseCardId);
            log.debug("기준 카드 {}에 대한 기존 추천 데이터 삭제 완료", baseCardId);

            // 2. 상위 5개만 선택하여 저장
            List<UserCardRecommendationVO> recommendations = recommendedCards.stream()
                .limit(5) // 최대 5개만 저장
                .map(card -> UserCardRecommendationVO.builder()
                    .userId(userId)
                    .baseCardId(baseCardId) // 기준 카드 ID 추가
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

            log.info("사용자 {}의 기준 카드 {}에 대한 추천 카드 {} 개 저장 완료", userId, baseCardId, recommendations.size());

        } catch (Exception e) {
            log.error("추천 카드 저장 중 오류 발생: 사용자 {}, 기준 카드 {}", userId, baseCardId, e);
            throw new RuntimeException("추천 카드 저장 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public List<CardBenefitDTO> getSavedRecommendations(Long userId, Integer baseCardId) {
        log.info("사용자 {}의 기준 카드 {}에 대한 저장된 추천 카드 조회", userId, baseCardId);

        try {
            List<UserCardRecommendationVO> recommendations = 
                userCardRecommendationMapper.selectRecommendationsByUserIdAndBaseCardId(userId, baseCardId);

            List<CardBenefitDTO> result = recommendations.stream()
                .map(this::convertToCardBenefitDTO)
                .collect(Collectors.toList());
            
            // 각 카드에 좋아요/즐겨찾기 상태 설정
            result.forEach(cardDto -> setLikeAndFavoriteStatus(userId, cardDto));

            log.info("사용자 {}의 기준 카드 {}에 대한 저장된 추천 카드 {} 개 조회 완료", userId, baseCardId, result.size());
            return result;

        } catch (Exception e) {
            log.error("저장된 추천 카드 조회 중 오류 발생: 사용자 {}, 기준 카드 {}", userId, baseCardId, e);
            throw new RuntimeException("저장된 추천 카드 조회 중 오류가 발생했습니다.", e);
        }
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
    public void deleteRecommendations(Long userId, Integer baseCardId) {
        log.info("사용자 {}의 기준 카드 {} 관련 추천 카드 삭제", userId, baseCardId);

        try {
            userCardRecommendationMapper.deleteRecommendationsByUserIdAndBaseCardId(userId, baseCardId);
            log.info("사용자 {}의 기준 카드 {} 관련 추천 카드 삭제 완료", userId, baseCardId);

        } catch (Exception e) {
            log.error("추천 카드 삭제 중 오류 발생: 사용자 {}, 기준 카드 {}", userId, baseCardId, e);
            throw new RuntimeException("추천 카드 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 사용자의 특정 기준 카드에 대한 추천 카드 개수를 조회합니다.
     */
    public int getRecommendationCount(Long userId, Integer baseCardId) {
        log.debug("사용자 {}의 기준 카드 {}에 대한 추천 카드 개수 조회", userId, baseCardId);
        
        try {
            return userCardRecommendationMapper.countRecommendationsByUserIdAndBaseCardId(userId, baseCardId);
        } catch (Exception e) {
            log.error("추천 카드 개수 조회 중 오류 발생: 사용자 {}, 기준 카드 {}", userId, baseCardId, e);
            return 0;
        }
    }

    /**
     * 사용자의 모든 추천 카드 개수를 조회합니다.
     */
    public int getAllRecommendationCount(Long userId) {
        log.debug("사용자 {}의 모든 추천 카드 개수 조회", userId);
        
        try {
            return userCardRecommendationMapper.countRecommendationsByUserId(userId);
        } catch (Exception e) {
            log.error("모든 추천 카드 개수 조회 중 오류 발생: 사용자 {}", userId, e);
            return 0;
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
    
    /**
     * 카드 DTO에 좋아요와 즐겨찾기 상태를 설정합니다.
     */
    private void setLikeAndFavoriteStatus(Long userId, CardBenefitDTO cardDto) {
        if (userId != null && cardDto.getCardId() != null) {
            try {
                // 좋아요 상태 조회
                boolean isLiked = cardDetailMapper.isLikedByUser(userId, cardDto.getCardId());
                int likeCount = cardDetailMapper.countLikesByProductId(cardDto.getCardId());
                cardDto.setLiked(isLiked);
                cardDto.setLikeCount(likeCount);
                
                // 즐겨찾기 상태 조회
                boolean isStarred = userMapper.isCardFavoriteExists(userId, Long.valueOf(cardDto.getCardId()));
                cardDto.setStarred(isStarred);
            } catch (Exception e) {
                log.warn("카드 {} 좋아요/즐겨찾기 상태 조회 실패: {}", cardDto.getCardId(), e.getMessage());
                // 기본값 설정
                cardDto.setLiked(false);
                cardDto.setLikeCount(0);
                cardDto.setStarred(false);
            }
        } else {
            // 기본값 설정
            cardDto.setLiked(false);
            cardDto.setLikeCount(0);
            cardDto.setStarred(false);
        }
    }
}