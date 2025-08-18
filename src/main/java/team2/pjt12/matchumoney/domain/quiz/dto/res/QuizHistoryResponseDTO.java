package team2.pjt12.matchumoney.domain.quiz.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizLogVO;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizProblemVO;

import java.time.LocalDateTime;

@Getter
@Builder
@ApiModel(value = "QuizHistoryResponse", description = "퀴즈 풀이 이력 응답 DTO")
public class QuizHistoryResponseDTO {

    @ApiModelProperty(value = "퀴즈 로그 ID", example = "101")
    private Long logId;

    @ApiModelProperty(value = "문제 내용 ", example = "금리는 기준금리와 연동된다.")
    private String quizText;

    @ApiModelProperty(value = "사용자의 선택", example = "true")
    private Boolean userAnswer;

    @ApiModelProperty(value = "사용자 답변", example = "true")
    private Boolean correctAnswer;

    @ApiModelProperty(value = "정답 여부", example = "true")
    private Boolean isCorrect;

    @ApiModelProperty(value = "문제 해설", example = "기준금리가 상승하면 일반 금리도 함께 오르는 경향이 있습니다.")
    private String explanation;

    @ApiModelProperty(value = "풀이 시각", example = "2025-08-18T12:34:56")
    private LocalDateTime solvedTime;

    public static QuizHistoryResponseDTO from(QuizLogVO quizLog, QuizProblemVO quizProblem) {
        return QuizHistoryResponseDTO.builder()
                .logId(quizLog.getLogId())
                .quizText(quizProblem.getQuizText())
                .userAnswer(quizLog.getUserAnswer())
                .correctAnswer(quizProblem.getQuizAnswer())
                .isCorrect(quizLog.getIsCorrect())
                .explanation(quizProblem.getExplanation())
                .solvedTime(quizLog.getSolvedTime())
                .build();
    }
}