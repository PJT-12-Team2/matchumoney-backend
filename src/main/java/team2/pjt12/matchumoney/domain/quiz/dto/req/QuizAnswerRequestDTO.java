package team2.pjt12.matchumoney.domain.quiz.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuizAnswerRequestDTO {

    private Long problemId;
    private Boolean userAnswer;
}