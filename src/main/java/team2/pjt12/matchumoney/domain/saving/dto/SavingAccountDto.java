package team2.pjt12.matchumoney.domain.saving.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SavingAccountDto {
    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "은행 ID", example = "0004")
    private Long finId;

    @ApiModelProperty(value = "계좌 번호", example = "11012345678")
    private String resAccount;

    @ApiModelProperty(value = "계좌 상태", example = "정상")
    private String resAccountStatus;

    @ApiModelProperty(value = "계좌번호 (표시)", example = "110-1234-5678")
    private String resAccountDisplay;

    @ApiModelProperty(value = "계좌명(종류)", example = "주거래적금")
    private String resAccountName;

    @ApiModelProperty(value = "계좌 별칭", example = "자동이체용")
    private String resAccountNickName;

    @ApiModelProperty(value = "예금주명", example = "홍길동")
    private String resAccountHolder;

    @ApiModelProperty(value = "최종 회차", example = "12")
    private String resFinalRoundNo;

    @ApiModelProperty(value = "계좌 개설일 (YYYYMMDD)", example = "20250101")
    private String resAccountStartDate;

    @ApiModelProperty(value = "계좌 만기일 (YYYYMMDD)", example = "20260101")
    private String resAccountEndDate;

    @ApiModelProperty(value = "계좌 잔액", example = "1500000")
    private Long resAccountBalance;

    @ApiModelProperty(value = "월 납입금", example = "100000")
    private Long resMonthlyPayment;

    @ApiModelProperty(value = "유효기간(개월)", example = "12")
    private String resValidPeriod;

    @ApiModelProperty(value = "적금 유형", example = "자유적립식")
    private String resType;

    @ApiModelProperty(value = "관리 지점", example = "강남지점")
    private String resManagementBranch;

    @ApiModelProperty(value = "적용 금리", example = "3.0%")
    private String resRate;

    @ApiModelProperty(value = "계약 금액", example = "10000")
    private Long resContractAmount;

    @ApiModelProperty(value = "납입 방식", example = "자동이체")
    private String resPaymentMethods;

    @ApiModelProperty(value = "최종 거래일 (YYYYMMDD)", example = "20250815")
    private String resLastTranDate;

    @ApiModelProperty(value = "시작 일자 (YYYYMMDD)", example = "20250101")
    private String commStartDate;

    @ApiModelProperty(value = "종료 일자 (YYYYMMDD)", example = "20260101")
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
