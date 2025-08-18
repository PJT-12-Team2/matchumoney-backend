package team2.pjt12.matchumoney.domain.savingsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
<<<<<<< HEAD
@ApiModel(value = "SavingOption", description = "적금 상품 금리 정보 DTO")
=======
@ApiModel(value = "SavingOption", description = "적금 상품 옵션 정보 DTO")
>>>>>>> 687abec (feat: swagger 설명 추가)
public class SavingOptionDTO {
    @ApiModelProperty(value = "저축 기간 (개월)", example = "12")
    private String saveTrm;

    @ApiModelProperty(value = "기본 금리", example = "2.3")
    private Double intrRate;

<<<<<<< HEAD
    @ApiModelProperty(value = "최대 금리", example = "3.55")
=======
    @ApiModelProperty(value = "최대 금리", example = "3.5")
>>>>>>> 687abec (feat: swagger 설명 추가)
    private Double intrRate2;
}
