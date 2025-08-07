package team2.pjt12.matchumoney.domain.quiz.dto.res;

import lombok.Builder;
import lombok.Getter;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizProblemVO;

@Getter
@Builder
public class QuizProblemResponseDTO {

    private Long problemId;
    private String quizText;
    private String explanation;

    public static QuizProblemResponseDTO from(QuizProblemVO quizProblem) {
        return QuizProblemResponseDTO.builder()
                .problemId(quizProblem.getProblemId())
                .quizText(quizProblem.getQuizText())
                .explanation(quizProblem.getExplanation())
                .build();
    }
}