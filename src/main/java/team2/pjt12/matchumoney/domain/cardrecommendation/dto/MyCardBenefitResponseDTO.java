package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyCardBenefitResponseDTO {
    private Long totalSpendAmount; // 최근 30일 총 사용금액
    private List<CategoryStatDTO> categoryStats; // 카테고리별 사용 통계
    private List<CardBenefitDTO> ownedCardBenefits; // 보유 카드별 예상 혜택
}