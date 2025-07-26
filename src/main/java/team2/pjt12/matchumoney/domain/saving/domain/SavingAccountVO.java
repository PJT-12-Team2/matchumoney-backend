package team2.pjt12.matchumoney.domain.saving.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor//이에 준하여 builder 생성
@NoArgsConstructor
@Builder
public class SavingAccountVO {
    private Long accountId;
    private String resAccount;
    private Long userId;
    private Long finId;
    private String resAccountStatus;
    private String resAccountDisplay;
    private String resAccountName;
    private String resAccountNickName;
    private String resAccountHolder;
    private String resFinalRoundNo;
    private String resAccountStartDate;
    private String resAccountEndDate;
    private Long resAccountBalance;
    private Long resMonthlyPayment;
    private String resValidPeriod;
    private String resType;
    private String resManagementBranch;
    private String resRate;
    private Long resContractAmount;
    private String resPaymentMethods;
    private String resLastTranDate;
    private String commStartDate;
    private String commEndDate;
    private LocalDateTime createdTime;
    private LocalDateTime lastModifiedTime;
}
