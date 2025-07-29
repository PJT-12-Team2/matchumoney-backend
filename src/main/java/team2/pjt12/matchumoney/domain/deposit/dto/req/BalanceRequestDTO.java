package team2.pjt12.matchumoney.domain.deposit.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceRequestDTO {
    private String userId;           // 사용자 ID
    private Long balance;            // 선택된 계좌의 잔액
    private String accountNumber;    // 선택된 계좌번호 (옵션, 로그용)

    public BalanceRequestDTO(String userId, Long balance) {
        this.userId = userId;
        this.balance = balance;
    }
}