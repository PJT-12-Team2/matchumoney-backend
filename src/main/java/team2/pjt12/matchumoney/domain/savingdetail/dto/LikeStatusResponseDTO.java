package team2.pjt12.matchumoney.domain.savingdetail.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "LikeStatusResponse", description = "적금 상품 좋아요 상태 응답 DTO")
public class LikeStatusResponseDTO {

    @ApiModelProperty(value = "좋아요 여부", example = "true")
    private boolean liked;

    @ApiModelProperty(value = "좋아요 개수", example = "25")
    private int likeCount;
}