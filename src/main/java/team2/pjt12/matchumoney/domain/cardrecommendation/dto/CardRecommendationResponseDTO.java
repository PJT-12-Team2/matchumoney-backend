package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "더 나은 카드 추천 응답 DTO")
public class CardRecommendationResponseDTO {
    @ApiModelProperty(value = "최근 30일 총 사용금액", example = "485000")
    private Long totalSpendAmount;

    @ApiModelProperty(value = "카테고리별 사용 통계")
    private List<CategoryStatDTO> categoryStats;

    @ApiModelProperty(value = "추천 카드 목록(상위 5개)")
    private List<CardBenefitDTO> recommendedCards;

    @ApiModelProperty(value = "추천 메시지")
    private String message;
}