package team2.pjt12.matchumoney.domain.auth.dto;

import lombok.Getter;

@Getter
public class LoginResDto {

    private final String accessToken;
    private final boolean isNewUser;

    public LoginResDto(String accessToken, boolean isNewUser) {
        this.accessToken = accessToken;
        this.isNewUser = isNewUser;
    }
}