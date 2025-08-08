package team2.pjt12.matchumoney.domain.quiz.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class QuizLogVO {

    private Long logId;
    private Long userId;
    private Long problemId;
    private Boolean userAnswer;
    private Boolean isCorrect;
    private LocalDateTime solvedTime;
    private LocalDate solvedDate;

    @Builder
    public QuizLogVO(Long userId, Long problemId, Boolean userAnswer, Boolean isCorrect, LocalDate solvedDate) {
        this.userId = userId;
        this.problemId = problemId;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
        this.solvedDate = solvedDate;
    }
}