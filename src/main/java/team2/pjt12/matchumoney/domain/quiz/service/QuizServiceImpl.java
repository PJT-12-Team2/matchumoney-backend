package team2.pjt12.matchumoney.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizLogVO;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizProblemVO;
import team2.pjt12.matchumoney.domain.quiz.dto.req.QuizAnswerRequestDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizProblemResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizResultResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizStatsResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.mapper.QuizLogMapper;
import team2.pjt12.matchumoney.domain.quiz.mapper.QuizProblemMapper;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizServiceImpl implements QuizService {

    private final QuizProblemMapper quizProblemMapper;
    private final QuizLogMapper quizLogMapper;
    private final UserMapper userMapper; // (1) UserMapper 의존성 추가

    @Override
    public QuizProblemResponseDTO getTodayQuiz(Long userId) {
        int unsolvedCount = quizProblemMapper.getUnsolvedCountByUserId(userId);

        if (unsolvedCount == 0) {
            throw new RuntimeException("풀 수 있는 문제가 더 이상 없습니다. 모든 문제를 완료했습니다!");
        }

        QuizProblemVO randomUnsolvedProblem = quizProblemMapper.findRandomUnsolvedProblem(userId);

        if (randomUnsolvedProblem == null) {
            throw new RuntimeException("퀴즈 문제를 가져오는데 실패했습니다.");
        }

        return QuizProblemResponseDTO.from(randomUnsolvedProblem);
    }

    @Override
    @Transactional
    public QuizResultResponseDTO submitAnswer(Long userId, QuizAnswerRequestDTO requestDTO) {
        QuizProblemVO problem = quizProblemMapper.findById(requestDTO.getProblemId());
        if (problem == null) {
            throw new RuntimeException("존재하지 않는 문제입니다.");
        }

        if (quizLogMapper.existsByUserIdAndProblemId(userId, requestDTO.getProblemId())) {
            throw new RuntimeException("이미 푼 문제입니다.");
        }

        boolean isCorrect = problem.getQuizAnswer().equals(requestDTO.getUserAnswer());
        int earnedXP = isCorrect ? 10 : 0;

        // (2) 정답시 경험치 증가
        if (isCorrect) {
            userMapper.updateUserExp(userId, earnedXP);
        }

        // 퀴즈 풀이 로그 저장
        QuizLogVO quizLog = QuizLogVO.builder()
                .userId(userId)
                .problemId(requestDTO.getProblemId())
                .userAnswer(requestDTO.getUserAnswer())
                .isCorrect(isCorrect)
                .solvedDate(LocalDate.now())
                .build();

        quizLogMapper.save(quizLog);

        return QuizResultResponseDTO.builder()
                .isCorrect(isCorrect)
                .correctAnswer(problem.getQuizAnswer())
                .explanation(problem.getExplanation())
                .earnedXP(earnedXP)
                .build();
    }

    @Override
    public QuizStatsResponseDTO getUserQuizStats(Long userId) {
        int correctCount = quizLogMapper.getCorrectCountByUserId(userId);
        int wrongCount = quizLogMapper.getWrongCountByUserId(userId);
        int streak = quizLogMapper.getCurrentStreakByUserId(userId);
        int totalXP = correctCount * 10;

        return QuizStatsResponseDTO.builder()
                .correct(correctCount)
                .wrong(wrongCount)
                .streak(streak)
                .totalXP(totalXP)
                .build();
    }

    @Override
    public boolean hasCompletedTodayQuiz(Long userId) {
        return quizLogMapper.existsByUserIdAndDate(userId, LocalDate.now());
    }
}
