package team2.pjt12.matchumoney.domain.user.dto.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import team2.pjt12.matchumoney.domain.user.domain.Gender;

import java.time.LocalDate;

@Getter
public final class UserResponseDTO {
    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private final Long userId;

    @ApiModelProperty(value = "사용자 이메일", example = "test@example.com")
    private final String email;

    @ApiModelProperty(value = "사용자 닉네임", example = "홍길동")
    private final String nickname;

    @ApiModelProperty(value = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImageUrl;

    @ApiModelProperty(value = "성별", example = "MALE")
    private final Gender gender;

    @ApiModelProperty(value = "생년월일", example = "1995-05-20")
    private final LocalDate birthDate;

    @ApiModelProperty(value = "소셜 로그인 여부", example = "true")
    private final Boolean isSocialLogin;

    @ApiModelProperty(value = "소셜 로그인 제공자", example = "KAKAO")
    private final String provider;

    @ApiModelProperty(value = "비밀번호 존재 여부", example = "false")
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