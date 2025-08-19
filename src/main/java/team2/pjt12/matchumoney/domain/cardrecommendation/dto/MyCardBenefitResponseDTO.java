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
@ApiModel(description = "보유 카드 혜택 응답 DTO")
public class MyCardBenefitResponseDTO {
    @ApiModelProperty(value = "최근 30일 총 사용금액", example = "485000")
    private Long totalSpendAmount;

    @ApiModelProperty(value = "카테고리별 사용 통계")
    private List<CategoryStatDTO> categoryStats;

    @ApiModelProperty(value = "보유 카드별 예상 혜택")
    private List<CardBenefitDTO> ownedCardBenefits;
}