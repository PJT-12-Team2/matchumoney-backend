package team2.pjt12.matchumoney.domain.cardrecommendation.service;

public interface CardRecommendationRefreshService {
    
    /**
     * 사용자의 거래내역이 업데이트되었을 때 추천 카드를 재계산합니다.
     * @param userId 사용자 ID
     */
    void refreshRecommendationsForUser(Long userId);
    
    /**
     * 특정 사용자의 특정 카드에 대한 추천을 재계산합니다.
     * @param userId 사용자 ID
     * @param cardId 카드 ID
     */
    void refreshRecommendationsForUserCard(Long userId, Integer cardId);
}