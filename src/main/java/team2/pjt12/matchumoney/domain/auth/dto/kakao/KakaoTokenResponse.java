package team2.pjt12.matchumoney.domain.auth.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "카카오 토큰 응답 DTO")
public class KakaoTokenResponse {

        @ApiModelProperty(value = "카카오 액세스 토큰", example = "vP88s2zO0uvNS8PaFTdpr4GYk2BEcI4z...")
        @JsonProperty("access_token")
        private final String accessToken;

        public KakaoTokenResponse(@JsonProperty("access_token") String accessToken) {
                this.accessToken = accessToken;
        }

}