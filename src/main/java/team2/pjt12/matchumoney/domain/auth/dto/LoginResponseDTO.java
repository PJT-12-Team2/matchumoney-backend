package team2.pjt12.matchumoney.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {

    private final String accessToken;
    private final Long userId;
    private final String nickname;
    private final Long personaId;
}