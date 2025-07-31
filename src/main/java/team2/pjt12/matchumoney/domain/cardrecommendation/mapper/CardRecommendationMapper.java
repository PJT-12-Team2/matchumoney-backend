package team2.pjt12.matchumoney.domain.cardrecommendation.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardProductVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardParsedBenefitVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardTransactionSummaryVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.UserCardRecommendationVO;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CardRecommendationMapper {
    
    /**
     * 사용자 카드 거래내역을 기반으로 카테고리별 통계를 조회합니다.
     * @param userId 사용자 ID
     * @param startDate 조회 시작일 (30일 전)
     * @param endDate 조회 종료일 (현재일)
     * @return 카테고리별 거래 통계 목록
     */
    List<CardTransactionSummaryVO> selectTransactionSummaryByUserId(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 사용자의 지난 30일 총 거래액을 조회합니다.
     * @param userId 사용자 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 총 거래액
     */
    Long selectTotalSpendByUserId(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 사용자가 보유한 카드 정보를 조회합니다.
     * @param userId 사용자 ID
     * @return 보유 카드 목록
     */
    List<CardProductVO> selectUserOwnedCards(@Param("userId") Long userId);
    
    /**
     * 카드 타입별로 발급 가능한 카드 목록을 조회합니다.
     * @param cardType 카드 타입 (신용, 체크)
     * @param excludeCardIds 제외할 카드 ID 목록 (사용자 보유 카드)
     * @return 추천 가능한 카드 목록
     */
    List<CardProductVO> selectAvailableCardsByType(
        @Param("cardType") String cardType,
        @Param("excludeCardIds") List<Integer> excludeCardIds
    );
    
    /**
     * 특정 카드의 파싱된 혜택 정보를 조회합니다.
     * @param cardId 카드 ID
     * @return 카드 혜택 목록
     */
    List<CardParsedBenefitVO> selectCardBenefitsByCardId(@Param("cardId") Integer cardId);
    
    /**
     * 여러 카드의 파싱된 혜택 정보를 일괄 조회합니다.
     * @param cardIds 카드 ID 목록
     * @return 카드 혜택 목록
     */
    List<CardParsedBenefitVO> selectCardBenefitsByCardIds(@Param("cardIds") List<Integer> cardIds);
    
    /**
     * 특정 카드의 상세 정보를 조회합니다.
     * @param cardId 카드 ID
     * @return 카드 상세 정보
     */
    CardProductVO selectCardById(@Param("cardId") Integer cardId);
    
    /**
     * 사용자의 특정 카드 거래내역을 기반으로 카테고리별 통계를 조회합니다.
     * @param userId 사용자 ID
     * @param cardId 특정 카드 ID
     * @param startDate 조회 시작일 (30일 전)
     * @param endDate 조회 종료일 (현재일)
     * @return 카테고리별 거래 통계 목록
     */
    List<CardTransactionSummaryVO> selectTransactionSummaryByUserAndCard(
        @Param("userId") Long userId,
        @Param("cardId") Integer cardId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    /**
     * 사용자의 특정 카드 지난 30일 총 거래액을 조회합니다.
     * @param userId 사용자 ID
     * @param cardId 특정 카드 ID
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일
     * @return 총 거래액
     */
    Long selectTotalSpendByUserAndCard(
        @Param("userId") Long userId,
        @Param("cardId") Integer cardId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // === 추천 카드 저장/관리 메서드들 ===
    
    /**
     * 사용자의 기존 추천 카드 데이터를 삭제합니다.
     * @param userId 사용자 ID
     * @param baseCardId 기준 카드 ID
     */
    void deleteUserCardRecommendations(
        @Param("userId") Long userId, 
        @Param("baseCardId") Integer baseCardId
    );
    
    /**
     * 추천 카드 데이터를 저장합니다.
     * @param recommendation 추천 카드 정보
     */
    void insertUserCardRecommendation(UserCardRecommendationVO recommendation);
    
    /**
     * 사용자의 저장된 추천 카드 목록을 조회합니다.
     * @param userId 사용자 ID
     * @param baseCardId 기준 카드 ID
     * @return 추천 카드 목록
     */
    List<UserCardRecommendationVO> selectUserCardRecommendations(
        @Param("userId") Long userId,
        @Param("baseCardId") Integer baseCardId
    );
    
    /**
     * 사용자의 모든 추천 카드 목록을 조회합니다.
     * @param userId 사용자 ID
     * @return 추천 카드 목록
     */
    List<UserCardRecommendationVO> selectAllUserCardRecommendations(
        @Param("userId") Long userId
    );
}