package team2.pjt12.matchumoney.domain.user.dto.res;

import lombok.Getter;
import team2.pjt12.matchumoney.domain.user.domain.Gender;

import java.time.LocalDate;
@Getter
public final class UserResponseDTO {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final Gender gender;
    private final LocalDate birthDate;
    private final Boolean isSocialLogin;
    private final String provider;
    private final Boolean hasPassword;

    public UserResponseDTO(
            Long userId,
            String email,
            String nickname,
            String profileImageUrl,
            Gender gender,
            LocalDate birthDate,
            Boolean isSocialLogin,
            String provider,
            Boolean hasPassword) {
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
        this.birthDate = birthDate;
        this.isSocialLogin = isSocialLogin;
        this.provider = provider;
        this.hasPassword = hasPassword;
    }
}