package team2.pjt12.matchumoney.domain.persona.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "추천 상품 유형 DTO")
public class RecommendationDTO {
    @ApiModelProperty(value = "추천 유형", example = "정기예금(1~2년)")
    private String title;
    @ApiModelProperty(value = "추천 유형 설명", example = "원금 보장과 확정 금리로 마음 편한 자산 운용")
    private String detail;
}
