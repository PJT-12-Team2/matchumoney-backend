package team2.pjt12.matchumoney.domain.quiz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.quiz.domain.QuizProblemVO;

import java.util.List;

@Mapper
public interface QuizProblemMapper {

    List<QuizProblemVO> findAll();
    
    QuizProblemVO findById(@Param("problemId") Long problemId);
    
    void save(QuizProblemVO quizProblem);
    
    void update(QuizProblemVO quizProblem);
    
    void deleteById(@Param("problemId") Long problemId);
    
    QuizProblemVO findRandomProblem();
    
    List<QuizProblemVO> findUnsolvedProblemsByUserId(@Param("userId") Long userId);
    
    QuizProblemVO findRandomUnsolvedProblem(@Param("userId") Long userId);
    
    int getTotalCount();
    
    int getUnsolvedCountByUserId(@Param("userId") Long userId);
    
    List<QuizProblemVO> findByLogIds(@Param("logIds") List<Long> logIds);
}