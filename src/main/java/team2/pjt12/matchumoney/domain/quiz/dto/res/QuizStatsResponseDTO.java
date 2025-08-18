package team2.pjt12.matchumoney.domain.quiz.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(value = "QuizStatsResponse", description = "퀴즈 통계 응답 DTO")
public class QuizStatsResponseDTO {

    @ApiModelProperty(value = "맞힌 문제 수", example = "15")
    private Integer correct;

    @ApiModelProperty(value = "틀린 문제 수", example = "5")
    private Integer wrong;

    @ApiModelProperty(value = "현재 연속 정답 횟수", example = "4")
    private Integer streak;

    @ApiModelProperty(value = "누적 경험치", example = "240")
    private Integer totalXP;
}