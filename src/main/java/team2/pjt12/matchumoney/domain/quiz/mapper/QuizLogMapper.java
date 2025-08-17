package team2.pjt12.matchumoney.domain.quiz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizLogVO;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface QuizLogMapper {

    void save(QuizLogVO quizLog);
    
    List<QuizLogVO> findByUserId(@Param("userId") Long userId);
    
    QuizLogVO findByUserIdAndProblemIdAndDate(
            @Param("userId") Long userId, 
            @Param("problemId") Long problemId, 
            @Param("solvedDate") LocalDate solvedDate
    );
    
    boolean existsByUserIdAndDate(@Param("userId") Long userId, @Param("solvedDate") LocalDate solvedDate);
    
    int getTodayQuizCountByUserId(@Param("userId") Long userId, @Param("solvedDate") LocalDate solvedDate);
    
    boolean existsByUserIdAndProblemId(@Param("userId") Long userId, @Param("problemId") Long problemId);
    
    int getCorrectCountByUserId(@Param("userId") Long userId);
    
    int getWrongCountByUserId(@Param("userId") Long userId);
    
    int getCurrentStreakByUserId(@Param("userId") Long userId);
    
    List<QuizLogVO> findRecentLogsByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    List<QuizLogVO> findRecentHistoryByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    void deleteById(@Param("logId") Long logId);
}