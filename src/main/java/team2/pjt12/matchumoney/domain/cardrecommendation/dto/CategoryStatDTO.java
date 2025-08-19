package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "카테고리별 사용 통계 DTO")
public class CategoryStatDTO {
    @ApiModelProperty(value = "카테고리명", example = "음식점", position = 1)
    private String category;

    @ApiModelProperty(value = "해당 카테고리 총 사용금액", example = "1250000", position = 2)
    private Long totalAmount;

    @ApiModelProperty(value = "거래 건수", example = "18", position = 3)
    private Integer transactionCount;

    @ApiModelProperty(value = "해당 카테고리 평균 거래금액(원)", example = "17850.75", position = 4)
    private BigDecimal averageAmount;

    @ApiModelProperty(
            value = "전체 사용액 대비 해당 카테고리 비율(%)",
            notes = "0~100 사이의 퍼센트 값",
            example = "23.5",
            position = 5
    )
    private BigDecimal categoryRatio;
}