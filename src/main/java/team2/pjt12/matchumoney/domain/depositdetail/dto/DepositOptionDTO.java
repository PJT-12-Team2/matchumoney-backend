package team2.pjt12.matchumoney.domain.depositdetail.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "예금 옵션(기간/금리) DTO")
public class DepositOptionDTO {

    @ApiModelProperty(value = "예금 옵션 ID", example = "101")
    private Long depositOptionId;

    @ApiModelProperty(value = "금리 유형 코드", example = "S") // S: 단리, M: 복리 등
    private String intrRateType;

    @ApiModelProperty(value = "금리 유형명", example = "단리")
    private String intrRateTypeNm;

    @ApiModelProperty(value = "기간(개월)", example = "12")
    private String saveTrm;

    @ApiModelProperty(value = "기본 금리(%)", example = "2.50")
    private String intrRate;

    @ApiModelProperty(value = "최대 금리(%)", example = "2.90")
    private String intrRate2;

    @ApiModelProperty(value = "예금 상품 ID(외래키)", example = "10")
    private Long depositProductId;

    @ApiModelProperty(value = "상품 코드", example = "WR0001B")
    private String finPrdtCd;

    @ApiModelProperty(value = "금융사 ID", example = "3")
    private Long finId;
}