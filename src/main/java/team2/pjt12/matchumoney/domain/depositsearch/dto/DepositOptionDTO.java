package team2.pjt12.matchumoney.domain.depositsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@ApiModel(description = "예금 금리 옵션 DTO")
public class DepositOptionDTO {
    @ApiModelProperty(value = "기간(개월)", example = "12")
    private String saveTrm;

    @ApiModelProperty(value = "기본 금리(%)", example = "2.50")
    private Double intrRate;

    @ApiModelProperty(value = "최대 금리(%)", example = "2.90")
    private Double intrRate2;
}
