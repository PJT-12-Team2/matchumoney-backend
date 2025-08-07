package team2.pjt12.matchumoney.domain.quiz.dto.res;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuizStatsResponseDTO {

    private Integer correct;
    private Integer wrong;
    private Integer streak;
    private Integer totalXP;
}