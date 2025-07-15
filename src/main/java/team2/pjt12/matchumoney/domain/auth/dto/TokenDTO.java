package team2.pjt12.matchumoney.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDTO {

    private final String accessToken;
    private final Long userId;
    private final String nickname;

    @Builder
    public TokenDTO(String accessToken, Long userId, String nickname) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.nickname = nickname;
    }
}