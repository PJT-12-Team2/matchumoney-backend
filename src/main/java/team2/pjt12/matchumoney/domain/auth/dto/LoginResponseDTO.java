package team2.pjt12.matchumoney.domain.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponseDTO {

    private final String accessToken;

    public LoginResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}