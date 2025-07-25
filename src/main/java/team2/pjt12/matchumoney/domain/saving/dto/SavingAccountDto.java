package team2.pjt12.matchumoney.domain.saving.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SavingAccountDto {
    private Long userId;
    private Long finId;
    private String resAccount;
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

    public SavingAccountDto(JsonNode data, Long userId, Long finId) {
        this.userId = userId;
        this.finId = finId;
        this.resAccount = data.path("resAccount").asText();
        this.resAccountStatus = data.path("resAccountStatus").asText();
        this.resAccountDisplay = data.path("resAccountDisplay").asText();
        this.resAccountName = data.path("resAccountName").asText();
        this.resAccountNickName = data.path("resAccountNickName").asText();
        this.resAccountHolder = data.path("resAccountHolder").asText();
        this.resFinalRoundNo = data.path("resFinalRoundNo").asText();
        this.resAccountStartDate = data.path("resAccountStartDate").asText();
        this.resAccountEndDate = data.path("resAccountEndDate").asText();
        this.resAccountBalance = parseLongSafe(data.path("resAccountBalance").asText());
        this.resMonthlyPayment = parseLongSafe(data.path("resMonthlyPayment").asText());
        this.resValidPeriod = data.path("resValidPeriod").asText();
        this.resType = data.path("resType").asText();
        this.resManagementBranch = data.path("resManagementBranch").asText();
        this.resRate = data.path("resRate").asText();
        this.resContractAmount = parseLongSafe(data.path("resContractAmount").asText());
        this.resPaymentMethods = data.path("resPaymentMethods").asText();
        this.resLastTranDate = data.path("resLastTranDate").asText();
        this.commStartDate = data.path("commStartDate").asText();
        this.commEndDate = data.path("commEndDate").asText();
    }

    private Long parseLongSafe(String value) {
        try {
            return value == null || value.isBlank() ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
