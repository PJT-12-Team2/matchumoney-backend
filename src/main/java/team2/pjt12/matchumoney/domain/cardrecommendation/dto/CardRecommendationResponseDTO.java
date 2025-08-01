package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardRecommendationResponseDTO {
    private Long totalSpendAmount; // 최근 30일 총 사용금액
    private List<CategoryStatDTO> categoryStats; // 카테고리별 사용 통계
    private List<CardBenefitDTO> recommendedCards; // 추천 카드 목록 (상위 5개)
    private String message; // 추천 메시지
}