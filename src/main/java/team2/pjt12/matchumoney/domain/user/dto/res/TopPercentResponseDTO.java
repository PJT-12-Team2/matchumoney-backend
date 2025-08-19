package team2.pjt12.matchumoney.domain.user.dto.res;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@AllArgsConstructor
@ApiModel(value = "TopPercentResponse", description = "전체 유저 누적 EXP 기준 상위 퍼센트")
public class TopPercentResponseDTO {

    @Min(0)
    @Max(100)
    @ApiModelProperty(value = "상위 퍼센트(정수 %, 0~100)", example = "4")
    private final int topPercent; // 예: 12 -> 상위 12%
}
