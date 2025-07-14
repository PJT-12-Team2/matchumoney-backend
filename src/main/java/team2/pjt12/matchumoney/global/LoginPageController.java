package team2.pjt12.matchumoney.global;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import team2.pjt12.matchumoney.global.oauth.KakaoOauthProperties;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginPageController {

    private final KakaoOauthProperties kakaoOauthProperties;

    @GetMapping("/login")
    public String loginPage(Model model) {

        log.info("=========== Kakao Login Page Accessed ===========");
        String kakaoLoginUrl = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + kakaoOauthProperties.getClientId()
                + "&redirect_uri=" + kakaoOauthProperties.getRedirectUri();

        model.addAttribute("kakaoLoginUrl", kakaoLoginUrl);

        return "login";
    }
}