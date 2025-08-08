package team2.pjt12.matchumoney.domain.quiz.service;

import team2.pjt12.matchumoney.domain.quiz.dto.req.QuizAnswerRequestDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizProblemResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizResultResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizStatsResponseDTO;

public interface QuizService {

    QuizProblemResponseDTO getTodayQuiz(Long userId);
    
    QuizResultResponseDTO submitAnswer(Long userId, QuizAnswerRequestDTO requestDTO);
    
    QuizStatsResponseDTO getUserQuizStats(Long userId);
    
    boolean hasCompletedTodayQuiz(Long userId);
}