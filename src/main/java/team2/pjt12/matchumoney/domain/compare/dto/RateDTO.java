package team2.pjt12.matchumoney.domain.compare.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "이율 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RateDTO {
    @JsonIgnore
    @ApiModelProperty(value = "상품 id", example = "12")
    private Long id;

    @ApiModelProperty(value = "기간(개월 수)", example = "12")
    private int period;

    @ApiModelProperty(value = "기본 금리", example = "2.5")
    private BigDecimal baseRate;

    @ApiModelProperty(value = "최대 금리", example = "2.9")
    private BigDecimal maxRate;

    @ApiModelProperty(value = "이자 계산 방식", example = "단리")
    private String interestType; // 단리 or 복리

    @ApiModelProperty(value = "적립 방식", example = "정액적립식")
    private String savingMethod; // 정액적립식 or 자유적립식
}
