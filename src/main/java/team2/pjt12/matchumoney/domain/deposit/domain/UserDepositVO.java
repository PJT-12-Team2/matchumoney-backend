package team2.pjt12.matchumoney.domain.deposit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDepositVO {
    private String accountNo; // 계좌번호
    private String accountName; //계좌 상품명
    private Long balance; // 계좌 잔액
    private String userId; // 사용자 id
    private String nickname;  // 이름

}
