package team2.pjt12.matchumoney.domain.quiz.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class QuizProblemVO {

    private Long problemId;
    private String quizText;
    private Boolean quizAnswer;
    private String explanation;
    private LocalDateTime createdTime;
    private LocalDateTime lastModifiedTime;

    @Builder
    public QuizProblemVO(Long problemId, String quizText, Boolean quizAnswer, String explanation) {
        this.problemId = problemId;
        this.quizText = quizText;
        this.quizAnswer = quizAnswer;
        this.explanation = explanation;
    }
}