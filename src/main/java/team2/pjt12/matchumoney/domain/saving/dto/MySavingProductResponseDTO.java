package team2.pjt12.matchumoney.domain.saving.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
@ApiModel(description = "사용자가  적금 상품 정보 DTO")
public class MySavingProductResponseDTO {
    @ApiModelProperty(value = "적금 상품 id", example = "1")
    private String id;

    @ApiModelProperty(value = "적금 상품 제목", example = "KB푸른바다적금")
    private String title;

    @ApiModelProperty(value = "적금 시작일 (YYYYMMDD)", example = "20240101")
    private String start_date;

    @ApiModelProperty(value = "적금 만기일 (YYYYMMDD)", example = "20250101")
    private String end_date;

    @ApiModelProperty(value = "적용 금리 (연 %)", example = "2.500")
    private String rate;
}

