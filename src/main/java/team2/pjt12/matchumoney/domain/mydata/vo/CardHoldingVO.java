package team2.pjt12.matchumoney.domain.mydata.vo;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardHoldingVO {
    private Long holdingId;
    private Integer cardId; // 카드 ID (카드고릴라 idx)
    private Long finId;
    private Integer discontinued; // 0: 정상, 1: 단종 (MySQL boolean 호환)
    private String cardName;
    private String resCardNo; // 123-***-456789
    private String resSleepYn; // Y/N
    private String resCardType; // 체크, 신용
    private String resTrafficYn;
    private String resImageLink;
    private String resIssueDate; // YYYYMMDD
    private String resValidPeriod; // YYYYMM
    private String resState; // 정상, 해지, 정지
    private Timestamp createdTime;
    private Timestamp lastModifiedTime;
    private Long userId;
    private String connectedId; // 마이데이터 발급 ID
}
