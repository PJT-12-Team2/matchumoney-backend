package team2.pjt12.matchumoney.domain.deposit.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team2.pjt12.matchumoney.domain.deposit.domain.UserDepositVO;

import java.text.DecimalFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDepositResponseDTO {
    private String accountNo;        // 계좌번호 (마스킹 처리된)
    private String accountName;      // 계좌명
    private String formattedBalance; // 포맷팅된 잔액 (예: 1,000,000원)
    private String nickname;  // 이름

    /**
     * VO에서 DTO로 변환하는 정적 팩토리 메소드
     * @param UserDepositVO 변환할 UserDepositVO 객체
     * @return 변환된 AccountResponseDTO 객체
     */
    public static UserDepositResponseDTO from(UserDepositVO UserDepositVO) {
        return UserDepositResponseDTO.builder()
                .accountNo(maskAccountNumber(UserDepositVO.getAccountNo()))
                .accountName(UserDepositVO.getAccountName())
                .formattedBalance(formatBalance(UserDepositVO.getBalance()))
                .nickname(UserDepositVO.getNickname())
                .build();
    }


    private static String maskAccountNumber(String accountNo) {
        if (accountNo == null || accountNo.length() < 8) {
            return accountNo;
        }
        int length = accountNo.length();
        return accountNo.substring(0, 5) + "****" + accountNo.substring(length-5);
    }

    /**
     * 잔액을 천단위 콤마가 포함된 문자열로 포맷팅
     * 예: 1000000 -> "1,000,000원"
     */
    private static String formatBalance(Long balance) {
        if (balance == null) {
            return "0원";
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(balance) + "원";
    }
}
