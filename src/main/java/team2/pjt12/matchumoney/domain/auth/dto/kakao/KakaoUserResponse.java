package team2.pjt12.matchumoney.domain.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "카카오 사용자 정보 응답 DTO")
public class KakaoUserResponse {

    @ApiModelProperty(value = "카카오 사용자 고유 ID", example = "1234567890")
    private final String id;

    @ApiModelProperty(value = "카카오 계정 정보")
    @JsonProperty("kakao_account")
    private final KakaoAccount kakaoAccount;

    public KakaoUserResponse(
            @JsonProperty("id") String id,
            @JsonProperty("kakao_account") KakaoAccount kakaoAccount
    ) {
        this.id = id;
        this.kakaoAccount = kakaoAccount;
    }

    @Getter
    @ApiModel(description = "카카오 계정 정보")
    public static class KakaoAccount {
        @ApiModelProperty(value = "이메일", example = "test@naver.com")
        private final String email;

        @ApiModelProperty(value = "프로필 정보")
        private final KakaoProfile profile;

        public KakaoAccount(
                @JsonProperty("email") String email,
                @JsonProperty("profile") KakaoProfile profile
        ) {
            this.email = email;
            this.profile = profile;
        }
    }

    @Getter
    @ApiModel(description = "카카오 프로필 정보")
    public static class KakaoProfile {
        @ApiModelProperty(value = "사용자 닉네임", example = "머니머니")
        private final String nickname;

        @ApiModelProperty(value = "프로필 이미지 URL", example = "https://k.kakaocdn.net/dn/.../img_640x640.jpg")
        @JsonProperty("profile_image_url")
        private final String profileImageUrl;

        public KakaoProfile(
                @JsonProperty("nickname") String nickname,
                @JsonProperty("profile_image_url") String profileImageUrl
        ) {
            this.nickname = nickname;
            this.profileImageUrl = profileImageUrl;
        }
    }
}