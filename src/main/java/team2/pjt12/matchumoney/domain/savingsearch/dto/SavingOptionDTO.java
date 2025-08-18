package team2.pjt12.matchumoney.domain.savingsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SavingOption", description = "적금 상품 옵션 정보 DTO")
public class SavingOptionDTO {
    @ApiModelProperty(value = "저축 기간 (개월)", example = "12")
    private String saveTrm;

    @ApiModelProperty(value = "기본 금리", example = "2.3")
    private Double intrRate;

    @ApiModelProperty(value = "최대 금리", example = "3.5")
    private Double intrRate2;
}
