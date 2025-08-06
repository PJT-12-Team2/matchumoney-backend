package team2.pjt12.matchumoney.domain.cardrecommendation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.UserCardRecommendationVO;

import java.util.List;

@Mapper
public interface UserCardRecommendationMapper {
    
    /**
     * 추천 카드 정보를 저장합니다.
     * @param recommendation 저장할 추천 카드 정보
     * @return 저장된 행의 수
     */
    int insertRecommendation(UserCardRecommendationVO recommendation);
    
    /**
     * 여러 추천 카드를 배치로 저장합니다.
     * @param recommendations 저장할 추천 카드 목록
     * @return 저장된 행의 수
     */
    int insertRecommendationsBatch(List<UserCardRecommendationVO> recommendations);
    
    /**
     * 사용자의 모든 추천 카드를 조회합니다.
     * @param userId 사용자 ID
     * @return 추천 카드 목록
     */
    List<UserCardRecommendationVO> selectRecommendationsByUserId(Long userId);
    
    /**
     * 사용자의 특정 기준 카드에 대한 추천 카드를 조회합니다.
     * @param userId 사용자 ID
     * @param baseCardId 기준 카드 ID
     * @return 추천 카드 목록 (최대 5개)
     */
    List<UserCardRecommendationVO> selectRecommendationsByUserIdAndBaseCardId(@Param("userId") Long userId, @Param("baseCardId") Integer baseCardId);
    
    /**
     * 사용자의 모든 추천 카드를 삭제합니다.
     * @param userId 사용자 ID
     * @return 삭제된 행의 수
     */
    int deleteRecommendationsByUserId(Long userId);
    
    /**
     * 사용자의 특정 기준 카드에 대한 추천 카드를 삭제합니다.
     * @param userId 사용자 ID
     * @param baseCardId 기준 카드 ID
     * @return 삭제된 행의 수
     */
    int deleteRecommendationsByUserIdAndBaseCardId(@Param("userId") Long userId, @Param("baseCardId") Integer baseCardId);
    
    /**
     * 사용자의 추천 카드 개수를 조회합니다.
     * @param userId 사용자 ID
     * @return 추천 카드 개수
     */
    int countRecommendationsByUserId(Long userId);
    
    /**
     * 사용자의 특정 기준 카드에 대한 추천 카드 개수를 조회합니다.
     * @param userId 사용자 ID
     * @param baseCardId 기준 카드 ID
     * @return 추천 카드 개수
     */
    int countRecommendationsByUserIdAndBaseCardId(@Param("userId") Long userId, @Param("baseCardId") Integer baseCardId);
}