package team2.pjt12.matchumoney.domain.quiz.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizLogVO;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizProblemVO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizHistoryResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.mapper.QuizLogMapper;
import team2.pjt12.matchumoney.domain.quiz.mapper.QuizProblemMapper;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizProblemMapper quizProblemMapper;

    @Mock
    private QuizLogMapper quizLogMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private QuizServiceImpl quizService;

    @Test
    @DisplayName("퀴즈 이력 조회 - 정상적으로 최대 5개 조회")
    void getQuizHistory_Success() {
        // given
        Long userId = 1L;
        
        List<QuizLogVO> mockLogs = Arrays.asList(
                createMockQuizLog(1L, 1L, true, LocalDateTime.now().minusDays(1)),
                createMockQuizLog(2L, 2L, false, LocalDateTime.now().minusDays(2)),
                createMockQuizLog(3L, 3L, true, LocalDateTime.now().minusDays(3))
        );
        
        List<QuizProblemVO> mockProblems = Arrays.asList(
                createMockQuizProblem(1L, "문제1", true, "설명1"),
                createMockQuizProblem(2L, "문제2", false, "설명2"),
                createMockQuizProblem(3L, "문제3", true, "설명3")
        );
        
        when(quizLogMapper.findRecentHistoryByUserId(userId, 5)).thenReturn(mockLogs);
        when(quizProblemMapper.findByLogIds(any())).thenReturn(mockProblems);
        
        // when
        List<QuizHistoryResponseDTO> result = quizService.getQuizHistory(userId);
        
        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getQuizText()).isEqualTo("문제1");
        assertThat(result.get(0).getIsCorrect()).isTrue();
        assertThat(result.get(1).getQuizText()).isEqualTo("문제2");
        assertThat(result.get(1).getIsCorrect()).isFalse();
    }

    @Test
    @DisplayName("퀴즈 이력 조회 - 이력이 없는 경우 빈 리스트 반환")
    void getQuizHistory_EmptyHistory() {
        // given
        Long userId = 1L;
        when(quizLogMapper.findRecentHistoryByUserId(userId, 5)).thenReturn(List.of());
        
        // when
        List<QuizHistoryResponseDTO> result = quizService.getQuizHistory(userId);
        
        // then
        assertThat(result).isEmpty();
    }

    private QuizLogVO createMockQuizLog(Long logId, Long problemId, Boolean isCorrect, LocalDateTime solvedTime) {
        return QuizLogVO.builder()
                .userId(1L)
                .problemId(problemId)
                .userAnswer(isCorrect)
                .isCorrect(isCorrect)
                .solvedDate(solvedTime.toLocalDate())
                .build();
    }

    private QuizProblemVO createMockQuizProblem(Long problemId, String quizText, Boolean quizAnswer, String explanation) {
        return QuizProblemVO.builder()
                .problemId(problemId)
                .quizText(quizText)
                .quizAnswer(quizAnswer)
                .explanation(explanation)
                .build();
    }
}