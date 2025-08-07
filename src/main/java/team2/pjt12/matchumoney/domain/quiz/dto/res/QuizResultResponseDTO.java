package team2.pjt12.matchumoney.domain.quiz.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizResultResponseDTO {

    private Boolean isCorrect;
    private Boolean correctAnswer;
    private String explanation;
    private Integer earnedXP;
}