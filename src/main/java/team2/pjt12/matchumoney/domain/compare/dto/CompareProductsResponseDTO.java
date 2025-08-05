package team2.pjt12.matchumoney.domain.compare.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@ApiModel(description = "상품 비교 응답 DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CompareProductsResponseDTO {
    @ApiModelProperty(value = "예금 상품 리스트")
    List<CompareDepositResponseDTO> deposits;
    @ApiModelProperty(value = "적금 상품 리스트")
    List<CompareSavingResponseDTO> savings;
    @ApiModelProperty(value = "카드 상품 리스트")
    List<CompareCardResponseDTO> cards;
}
