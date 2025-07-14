package team2.pjt12.matchumoney.domain.auth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import team2.pjt12.matchumoney.domain.auth.dto.SocialUserInfo;
import team2.pjt12.matchumoney.domain.auth.dto.kakao.KakaoTokenResponse;
import team2.pjt12.matchumoney.domain.auth.dto.kakao.KakaoUserResponse;
import team2.pjt12.matchumoney.global.oauth.KakaoOauthProperties;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements SocialApiClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final KakaoOauthProperties kakaoOauthProperties;

    public SocialUserInfo getUserInfoByCode(String code) {
        String accessToken = getAccessToken(code);
        return getUserInfo(accessToken);
    }

    private String getAccessToken(String code) {
        String url = kakaoOauthProperties.getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOauthProperties.getClientId());
        params.add("redirect_uri", kakaoOauthProperties.getRedirectUri());
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                url, request, KakaoTokenResponse.class
        );

        return response.getBody().getAccessToken();
    }

    private SocialUserInfo getUserInfo(String accessToken) {
        String url = kakaoOauthProperties.getUserInfoUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                KakaoUserResponse.class
        );

        KakaoUserResponse kakaoUser = response.getBody();

        return new SocialUserInfo(
                String.valueOf(kakaoUser.getId()),
                kakaoUser.getKakaoAccount().getEmail(),
                kakaoUser.getKakaoAccount().getProfile().getNickname(),
                kakaoUser.getKakaoAccount().getProfile().getProfileImageUrl()
        );
    }
}