package team2.pjt12.matchumoney.domain.quiz.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizProblemVO;

@Getter
@Builder
@ApiModel(value = "QuizProblemResponse", description = "퀴즈 문제 응답 DTO")
public class QuizProblemResponseDTO {

    @ApiModelProperty(value = "문제 ID", example = "101")
    private Long problemId;

    @ApiModelProperty(value = "문제 내용", example = "금리는 기준금리와 연동된다.")
    private String quizText;

    @ApiModelProperty(value = "문제 해설", example = "기준금리가 상승하면 일반 금리도 함께 오르는 경향이 있습니다.")
    private String explanation;

    public static QuizProblemResponseDTO from(QuizProblemVO quizProblem) {
        return QuizProblemResponseDTO.builder()
                .problemId(quizProblem.getProblemId())
                .quizText(quizProblem.getQuizText())
                .explanation(quizProblem.getExplanation())
                .build();
    }
}