package team2.pjt12.matchumoney.domain.saving.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

        this.resAccount = text(data, "resAccount", "");
        this.resAccountBalance = numberOrZero(data, "resAccountBalance"); // "430115" 유지, null→"0"
        this.resAccountEndDate = text(data, "resAccountEndDate", "");
        // 키 불일치 가드: NickName / Nickname 둘 다 지원
        this.resAccountNickname = firstNonBlank(
                data.path("resAccountNickName").asText(null),
                data.path("resAccountNickname").asText(null),
                ""
        );
        this.resAccountStartDate = text(data, "resAccountStartDate", "");
        this.resLastTranDate = text(data, "resLastTranDate", "");
        this.resAccountDeposit = text(data, "resAccountDeposit", ""); // 11=예금, 12/14=적금
        this.resAccountName = text(data, "resAccountName", "");
        this.resAccountDisplay = text(data, "resAccountDisplay", "");
        this.resLoanEndDate = text(data, "resLoanEndDate", "");
        this.resAccountCurrency = text(data, "resAccountCurrency", "KRW");
        this.resAccountLifetime = text(data, "resAccountLifetime", "");
        this.resOverdraftAcctYN = text(data, "resOverdraftAcctYN", "0");
        this.resLoanKind = text(data, "resLoanKind", "");
        this.resLoanBalance = numberOrZero(data, "resLoanBalance");
        this.resLoanStartDate = text(data, "resLoanStartDate", "");

        // DB 컬럼이 VARCHAR면 문자열로 두셔도 되고, DATETIME이면 LocalDateTime으로 유지
        this.createdTime = String.valueOf(java.time.LocalDateTime.now());
        this.lastModifiedTime = String.valueOf(java.time.LocalDateTime.now());
    }

    private static String text(JsonNode n, String key, String def) {
        String v = n.path(key).asText(null);
        return (v == null) ? def : v;
    }

    private static String numberOrZero(JsonNode n, String key) {
        String v = n.path(key).asText(null);
        if (v == null) return "0";
        v = v.trim();
        if (v.isEmpty()) return "0";
        return v.replace(",", "");
    }

    private static String firstNonBlank(String... vals) {
        if (vals == null) return "";
        for (String v : vals) {
            if (v != null && !v.trim().isEmpty()) return v;
        }
        return "";
    }

}
