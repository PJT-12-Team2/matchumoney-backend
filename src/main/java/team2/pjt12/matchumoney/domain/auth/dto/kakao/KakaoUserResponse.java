package team2.pjt12.matchumoney.domain.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoUserResponse {

    private final String id;

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
    public static class KakaoAccount {
        private final String email;
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
    public static class KakaoProfile {
        private final String nickname;

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