package team2.pjt12.matchumoney.domain.mydata.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.mydata.vo.CardTransactionVO;

import java.util.List;

@Mapper
public interface KbCardTransactionMapper {
    /**
     * 거래 내역을 데이터베이스에 삽입합니다.
     * res_member_store_type 필드가 포함되어 자동 분류된 카테고리가 저장됩니다.
     */
    void insertKbCardTransaction(CardTransactionVO transaction);

    /**
     * 특정 holdingId에 해당하는 모든 거래 내역을 삭제합니다.
     */
    void deleteKbCardTransactionsByHoldingId(Long holdingId);

    /**
     * 사용자 ID와 holdingId로 거래 내역을 조회합니다.
     * res_member_store_type 필드를 포함하여 카테고리 정보도 함께 조회됩니다.
     */
    List<CardTransactionVO> selectKbCardTransactionsByUserIdAndHoldingId(Long userId, Long holdingId);

    /**
     * 특정 거래 내역의 카테고리를 업데이트합니다.
     */
    void updateTransactionCategory(@Param("transactionId") Long transactionId, @Param("category") String category);

    /**
     * 카테고리가 설정되지 않은 거래 내역을 조회합니다.
     */
    List<CardTransactionVO> selectTransactionsWithoutCategory(@Param("userId") Long userId);

    /**
     * 동일한 가맹점명을 가진 모든 거래 내역의 카테고리를 일괄 업데이트합니다.
     */
    void updateAllTransactionsCategory(@Param("merchantName") String merchantName, @Param("category") String category);
    
    /**
     * 거래내역 중복 체크를 위한 메서드
     * 사용자ID, 카드번호, 거래일, 거래시간, 승인번호를 기준으로 기존 거래내역 존재 여부 확인
     */
    boolean existsTransaction(@Param("userId") Long userId, 
                            @Param("cardNo") String cardNo,
                            @Param("usedDate") String usedDate, 
                            @Param("usedTime") String usedTime,
                            @Param("approvalNo") String approvalNo);
}
