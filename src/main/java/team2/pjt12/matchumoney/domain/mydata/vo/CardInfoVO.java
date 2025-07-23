package team2.pjt12.matchumoney.domain.mydata.vo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardInfoVO {
    private Long holdingId;
    private Integer cardId;
    private Long finId;
    private Integer discontinued;
    private String cardName;
    private String resCardNo;
    private String resSleepYn;
    private String resCardType;
    private String resTrafficYn;
    private String resImageLink;
    private String resIssueDate;
    private String resValidPeriod;
    private String resState;
    private java.sql.Timestamp createdTime;
    private java.sql.Timestamp lastModifiedTime;
    private Long userId;
}
