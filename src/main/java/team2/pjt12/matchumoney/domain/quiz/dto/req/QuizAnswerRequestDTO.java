package team2.pjt12.matchumoney.domain.quiz.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@ApiModel(value = "QuizAnswerRequest", description = "퀴즈 정답 제출 요청 DTO")
public class QuizAnswerRequestDTO {
    @ApiModelProperty(value = "문제 ID", example = "101")
    private Long problemId;

    @ApiModelProperty(value = "사용자 선택 (true: O, false: X)", example = "true")
    private Boolean userAnswer;
}