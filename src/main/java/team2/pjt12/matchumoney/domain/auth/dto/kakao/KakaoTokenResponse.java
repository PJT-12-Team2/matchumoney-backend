package team2.pjt12.matchumoney.domain.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class KakaoTokenResponse {

        @JsonProperty("access_token")
        private final String accessToken;

        public KakaoTokenResponse(@JsonProperty("access_token") String accessToken) {
                this.accessToken = accessToken;
        }

}