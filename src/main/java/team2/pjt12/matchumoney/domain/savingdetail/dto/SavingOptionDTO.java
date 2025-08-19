package team2.pjt12.matchumoney.domain.savingdetail.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SavingOption", description = "적금 상품 금리 옵션 DTO")
public class SavingOptionDTO {

    @ApiModelProperty(value = "적금 옵션 ID", example = "101")
    private Long savingOptionId;

    @ApiModelProperty(value = "금리 유형 코드", example = "3")
    private String intrRateType;

    @ApiModelProperty(value = "금리 유형", example = "단리")
    private String intrRateTypeNm;

    @ApiModelProperty(value = "저축 기간(개월)", example = "12")
    private String saveTrm;

    @ApiModelProperty(value = "기본 금리(%)", example = "3.0")
    private String intrRate;

    @ApiModelProperty(value = "최대 금리(%)", example = "3.5")
    private String intrRate2;

    @ApiModelProperty(value = "금융 상품 코드", example = "10141114300011")
    private String finPrdtCd;

    @ApiModelProperty(value = "금융사 ID", example = "16")
    private Long finId;
}