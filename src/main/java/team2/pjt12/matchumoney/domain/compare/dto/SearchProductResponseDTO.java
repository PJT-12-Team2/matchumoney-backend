package team2.pjt12.matchumoney.domain.compare.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchProductResponseDTO {
    @ApiModelProperty(value = "상품 ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "회사 이름", example = "국민은행")
    private String korCoName;

    @ApiModelProperty(value = "상품 이름", example = "KB푸른바다적금")
    private String finPrdtName;

    @ApiModelProperty(value = "이미지 URL", example = "/src/assets/bank-Logos/BK_KB_Profile.png")
    private String image;
}
