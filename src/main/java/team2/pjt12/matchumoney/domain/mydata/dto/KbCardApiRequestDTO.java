package team2.pjt12.matchumoney.domain.mydata.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class KbCardApiRequestDTO {
    private Long userId;
    private String cardId; // 국민카드 홈페이지 로그인 ID
    private String cardPw; // 국민카드 홈페이지 로그인 PW
}
