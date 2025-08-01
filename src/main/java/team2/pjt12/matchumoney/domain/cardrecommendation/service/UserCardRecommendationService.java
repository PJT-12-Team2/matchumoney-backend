package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CardBenefitDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.UserCardRecommendationVO;

import java.util.List;

public interface UserCardRecommendationService {
    
    /**
     * 추천 카드 목록을 데이터베이스에 저장합니다.
     * @param userId 사용자 ID
     * @param baseCardId 기준 카드 ID
     * @param recommendedCards 추천된 카드 목록
     */
    void saveRecommendations(Long userId, Integer baseCardId, List<CardBenefitDTO> recommendedCards);
    
    /**
     * 저장된 추천 카드 목록을 조회합니다.
     * @param userId 사용자 ID
     * @param baseCardId 기준 카드 ID
     * @return 저장된 추천 카드 목록
     */
    List<CardBenefitDTO> getSavedRecommendations(Long userId, Integer baseCardId);
    
    /**
     * 사용자의 모든 저장된 추천 카드를 조회합니다.
     * @param userId 사용자 ID
     * @return 모든 추천 카드 목록
     */
    List<UserCardRecommendationVO> getAllSavedRecommendations(Long userId);
    
    /**
     * 사용자의 특정 기준 카드에 대한 추천 데이터를 삭제합니다.
     * @param userId 사용자 ID
     * @param baseCardId 기준 카드 ID
     */
    void deleteRecommendations(Long userId, Integer baseCardId);
}