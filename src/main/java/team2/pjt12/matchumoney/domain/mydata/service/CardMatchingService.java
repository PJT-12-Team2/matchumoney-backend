package team2.pjt12.matchumoney.domain.mydata.service;

import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.dto.res.CardMatchingResultDTO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardVO;

import java.util.List;
import java.util.Optional;

public interface CardMatchingService {
    
    /**
     * 모든 카드 보유 정보의 card_id 매칭 수행
     * @return 매칭 결과 (성공 건수, 실패 건수 등)
     */
    CardMatchingResultDTO matchAllCardHoldings();
    
    /**
     * 특정 사용자의 카드 보유 정보 card_id 매칭 수행
     * @param userId 사용자 ID
     * @return 매칭 결과
     */
    CardMatchingResultDTO matchCardHoldingsByUserId(Long userId);
    
    /**
     * 매칭되지 않은 카드 보유 정보 목록 조회
     * @return 매칭되지 않은 카드 목록
     */
    List<CardHoldingVO> getUnmatchedCardHoldings();
    
    /**
     * 특정 카드 보유 정보에 수동으로 card_id 설정
     * @param holdingId 카드 보유 ID
     * @param cardId 카드 ID
     * @return 성공 여부
     */
    boolean manualMatchCardHolding(Long holdingId, Integer cardId);
    
    /**
     * 카드명 매칭 테스트
     * @param cardName 테스트할 카드명
     * @return 매칭된 카드 정보
     */
    Optional<CardVO> testCardMatching(String cardName);
    
    /**
     * 특정 사용자의 모든 카드 보유 정보 조회 (디버그용)
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 카드 보유 정보
     */
    List<CardHoldingVO> getAllCardHoldingsByUserId(Long userId);
}
