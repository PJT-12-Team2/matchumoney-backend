package team2.pjt12.matchumoney.domain.saving.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor//이에 준하여 builder 생성
@NoArgsConstructor
@Builder
public class DepositAccountVO {
    private Long accountId;

    private String resAccount; // 계좌번호
    private Long userId;
    private Long finId;
    private String resAccountBalance;
    private String resAccountEndDate;
    private String resAccountNickname;
    private String resAccountStartDate;
    private String resLastTranDate;
    private String resAccountDeposit;
    private String resAccountName;
    private String resAccountDisplay;
    private String resLoanEndDate;

    private String resAccountCurrency;
    private String resAccountLifetime;
    private String resOverdraftAcctYN;
    private String resLoanKind;
    private String resLoanBalance;
    private String resLoanStartDate;

    private String createdTime;
    private String lastModifiedTime;

    public DepositAccountVO(JsonNode data, Long userId, Long finId) {
        this.userId = userId;
        this.finId = finId;

        this.resAccount = data.path("resAccount").asText(null);
        this.resAccountBalance = data.path("resAccountBalance").asText(null);
        this.resAccountEndDate = data.path("resAccountEndDate").asText(null);
        this.resAccountNickname = data.path("resAccountNickname").asText(null);
        this.resAccountStartDate = data.path("resAccountStartDate").asText(null);
        this.resLastTranDate = data.path("resLastTranDate").asText(null);
        this.resAccountDeposit = data.path("resAccountDeposit").asText(null);
        this.resAccountName = data.path("resAccountName").asText(null);
        this.resAccountDisplay = data.path("resAccountDisplay").asText(null);
        this.resLoanEndDate = data.path("resLoanEndDate").asText(null);
        this.resAccountCurrency = data.path("resAccountCurrency").asText(null);
        this.resAccountLifetime = data.path("resAccountLifetime").asText(null);
        this.resOverdraftAcctYN = data.path("resOverdraftAcctYN").asText(null);
        this.resLoanKind = data.path("resLoanKind").asText(null);
        this.resLoanBalance = data.path("resLoanBalance").asText(null);
        this.resLoanStartDate = data.path("resLoanStartDate").asText(null);
        this.createdTime = String.valueOf(LocalDateTime.now());
        this.lastModifiedTime = String.valueOf(LocalDateTime.now());
    }

}
