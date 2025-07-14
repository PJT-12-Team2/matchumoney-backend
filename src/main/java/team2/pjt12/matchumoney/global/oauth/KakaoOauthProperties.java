package team2.pjt12.matchumoney.global.oauth;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class KakaoOauthProperties {
    @Value("${oauth.clientId}")
    private String clientId;
    @Value("${oauth.redirectUri}")
    private String redirectUri;
    @Value("${oauth.tokenUri}")
    private String tokenUri;
    @Value("${oauth.userInfoUri}")
    private String userInfoUri;
}
