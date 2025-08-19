package team2.pjt12.matchumoney.domain.carddetail.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "카드 혜택 옵션 DTO")
public class CardOptionDTO {
    @ApiModelProperty(value = "파싱된 혜택 ID", example = "101")
    private int parsedBenefitId;

    @ApiModelProperty(value = "카드 ID", example = "10")
    private int cardId2;

    @ApiModelProperty(value = "혜택 제목", example = "쇼핑")
    private String title;

    @ApiModelProperty(value = "혜택 카테고리", example = "온라인쇼핑")
    private String category;

    @ApiModelProperty(value = "혜택 유형(할인/적립 등)", example = "할인")
    private String benefitType;

    @ApiModelProperty(value = "혜택 값(율/금액 등)", example = "10.00")
    private double value;

    @ApiModelProperty(value = "혜택 조건", example = "온라인")
    private String conditionText;

    @ApiModelProperty(value = "월 최대 혜택(원)", example = "50000")
    private Integer maxBenefitMonthly;

    @ApiModelProperty(value = "건당 최소 결제금액(원)", example = "5000")
    private Integer minSpendPerTransaction;

    @ApiModelProperty(value = "해당 혜택 전월 실적(원)", example = "300000")
    private Integer preMonthMoneySpecific;

    @ApiModelProperty(value = "혜택 상세 설명", example = "국내 유명 백화점, 할인점, 온라인 쇼핑몰 최대 5% 할인...")
    private String description;
}