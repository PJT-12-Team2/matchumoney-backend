package team2.pjt12.matchumoney.domain.compare.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "카드 혜택 옵션 DTO")
public class CardOptionDTO {
    @ApiModelProperty(value = "혜택 ID", example = "1")
    private int parsedBenefitId;

    @ApiModelProperty(value = "카드 ID", example = "10")
    private long cardId2;

    @ApiModelProperty(value = "혜택 제목", example = "쇼핑")
    private String title;

    @ApiModelProperty(value = "혜택 카테고리", example = "온라인쇼핑")
    private String category;

    @ApiModelProperty(value = "혜택 유형", example = "적립") // or 할인
    private String benefitType;

    @ApiModelProperty(value = "혜택 비율 또는 금액", example = "5")
    private double value;

    @ApiModelProperty(value = "혜택 조건 설명", example = "온라인")
    private String conditionText;

    @ApiModelProperty(value = "월 최대 혜택 한도 (원)", example = "5000")
    private Integer maxBenefitMonthly;

    @ApiModelProperty(value = "건당 최소 이용금액", example = "10000")
    private Integer minSpendPerTransaction;

    @ApiModelProperty(value = "전월 실적 조건", example = "300000")
    private Integer preMonthMoneySpecific;
}
