package team2.pjt12.matchumoney.domain.quiz.dto.res;

import lombok.Builder;
import lombok.Getter;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizLogVO;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizProblemVO;

import java.time.LocalDateTime;

@Getter
@Builder
public class QuizHistoryResponseDTO {
    
    private Long logId;
    private String quizText;
    private Boolean userAnswer;
    private Boolean correctAnswer;
    private Boolean isCorrect;
    private String explanation;
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