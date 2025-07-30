package team2.pjt12.matchumoney.domain.mydata.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardVO;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CardMatchingMapper {

    /**
     * 입력된 카드명과 가장 일치하는 카드 정보를 조회합니다.
     * 우선순위:
     * 1. 정확히 일치하는 카드명
     * 2. 괄호 안의 내용을 제거한 카드명이 일치하는 경우
     * @param inputCardName 매칭할 카드명 (예: "나라사랑체크카드(일반형 RF)")
     * @return 매칭된 카드 정보 (없을 경우 Optional.empty())
     */
    Optional<CardVO> findMatchingCardByName(@Param("inputCardName") String inputCardName);

    /**
     * mydata_card_holdings 테이블의 card_id가 null인 레코드들 조회
     * @return card_id가 매칭되지 않은 카드 보유 정보 목록
     */
    List<CardHoldingVO> findUnmatchedCardHoldings();

    /**
     * mydata_card_holdings 테이블의 특정 holding_id에 card_id 업데이트
     * @param holdingId 카드 보유 ID
     * @param cardId 매칭된 카드 ID
     * @return 업데이트된 행 수
     */
    int updateCardIdForHolding(@Param("holdingId") Long holdingId, @Param("cardId") Integer cardId);

    /**
     * 모든 mydata_card_holdings의 card_id를 일괄 매칭 및 업데이트
     * @return 업데이트된 행 수
     */
    int updateAllCardMatching();

    /**
     * 특정 사용자의 카드 보유 정보에서 card_id 매칭 업데이트
     * @param userId 사용자 ID
     * @return 업데이트된 행 수
     */
    int updateCardMatchingByUserId(@Param("userId") Long userId);

    /**
     * 특정 카드명으로 매칭 테스트
     * @param cardName 테스트할 카드명
     * @return 매칭된 카드 정보 (있는 경우)
     */
    Optional<CardVO> testCardMatching(@Param("cardName") String cardName);

    /**
     * 디버그용: 특정 사용자의 모든 카드 보유 정보 조회
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 카드 보유 정보
     */
    List<CardHoldingVO> findAllCardHoldingsByUserId(@Param("userId") Long userId);
}