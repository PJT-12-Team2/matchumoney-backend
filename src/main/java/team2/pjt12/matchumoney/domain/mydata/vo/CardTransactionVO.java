package team2.pjt12.matchumoney.domain.mydata.vo;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardTransactionVO {
    private Long transactionId;
    private Long finId;
    private Long holdingId; // mydata_card_holdings의 holding_id
    private Integer cardId2; // mydata_card_holdings의 card_id (카드고릴라 idx)
    private Long userId;
    private String cardName; // 카드고릴라 기준 카드 이름 (CardInfoVO에서 가져옴)
    private String resUsedDate;
    private String resUsedTime;
    private String resCardNo;
    private String resCardNo1; // 추가 정보 (CodeF 응답에 따라)
    private String resCardName; // 국민카드사에서 응답하는 카드 이름
    private String resMemberStoreName;
    private Long resUsedAmount;
    private String resPaymentType;
    private String resInstallmentMonth;
    private String resApprovalNo;
    private String resPaymentDueDate;
    private String resHomeForeignType;
    private String resMemberStoreCorpNo;
    private String resMemberStoreType;
    private String resMemberStoreTelNo;
    private String resMemberStoreAddr;
    private String resMemberStoreNo;
    private String resCancelYn;
    private Long resCancelAmount;
    private Long resVat;
    private Long resCashBack;
    private Long resKrwAmt;
    private String commStartDate;
    private String commEndDate;
    private String resAccountCurrency;
    private Timestamp createdTime;
    private Timestamp lastModifiedTime;
}
