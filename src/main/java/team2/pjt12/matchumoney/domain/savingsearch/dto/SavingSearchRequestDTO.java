package team2.pjt12.matchumoney.domain.savingsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SavingSearchRequest", description = "적금 상품 검색 요청 DTO")
public class SavingSearchRequestDTO {
    @ApiModelProperty(value = "은행 이름", example = "신한은행")
    private String korCoNm;

    @ApiModelProperty(value = "저축 금액", example = "100000")
    private Integer maxLimit;
}