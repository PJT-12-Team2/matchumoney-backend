package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CardRecommendationResponseDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.MyCardBenefitResponseDTO;

import java.util.List;

public interface CardRecommendationService {
    
    /**
     * 사용자의 특정 카드 사용 내역을 기반으로 해당 카드의 혜택을 계산합니다.
     * @param userId 사용자 ID
     * @param cardId 특정 카드 ID
     * @return 특정 카드 혜택 정보
     */
    MyCardBenefitResponseDTO calculateSpecificCardBenefit(Long userId, Integer cardId);
    
    /**
     * 사용자의 특정 카드 소비 패턴을 기반으로 더 나은 혜택을 제공하는 상위 5개 카드를 추천합니다.
     * @param userId 사용자 ID
     * @param cardId 기준이 되는 특정 카드 ID
     * @return 추천 카드 목록
     */
    CardRecommendationResponseDTO recommendBetterCards(Long userId, Integer cardId);
    
    /**
     * 사용자가 보유한 모든 카드의 혜택을 조회합니다.
     * 저장된 추천 데이터가 있으면 데이터베이스에서 조회하고, 없으면 실시간 계산합니다.
     * @param userId 사용자 ID
     * @return 보유 카드별 혜택 정보 목록
     */
    List<MyCardBenefitResponseDTO> getMyCardsBenefits(Long userId);
    
    /**
     * 특정 카드의 저장된 추천 데이터를 조회합니다.
     * 데이터베이스에 저장된 추천 결과를 반환합니다.
     * @param userId 사용자 ID
     * @param cardId 카드 ID
     * @return 저장된 추천 카드 목록
     */
    CardRecommendationResponseDTO getSavedRecommendations(Long userId, Integer cardId);
    
    /**
     * 사용자의 보유 카드에 대해 추천 카드를 자동 생성하고 저장합니다.
     * 거래내역이 업데이트될 때 호출됩니다.
     * @param userId 사용자 ID
     * @param cardId 거래내역이 업데이트된 카드 ID
     */
    void generateAndSaveRecommendations(Long userId, Integer cardId);
}