package team2.pjt12.matchumoney.domain.carddetail.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "카드 좋아요 상태 응답 DTO")
public class LikeStatusResponseDTO {
    @ApiModelProperty(value = "해당 카드 좋아요 여부", example = "true")
    private boolean liked;

    @ApiModelProperty(value = "해당 카드 좋아요 수", example = "13")
    private int likeCount;
}