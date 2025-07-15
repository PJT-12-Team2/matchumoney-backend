package team2.pjt12.matchumoney.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.auth.dto.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.LoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.SignupRequestDTO;
import team2.pjt12.matchumoney.domain.auth.service.AuthService;
import team2.pjt12.matchumoney.domain.user.service.UserService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao-login")
    public SuccessResponse<LoginResponseDTO> kakaoLogin(@RequestBody SocialLoginRequestDTO request) {
        LoginResponseDTO response = authService.loginOrSignUp(request);
        return new SuccessResponse<>(response);
    }

    @PostMapping("/signup")
    public SuccessResponse<?> signup(@RequestBody @Valid SignupRequestDTO reqDto) {
        authService.signup(reqDto);
        return new SuccessResponse<>("회원가입 성공");
    }

    @PostMapping("/login")
    public SuccessResponse<TokenDTO> signin(@RequestBody @Valid LoginRequestDTO reqDto, HttpServletResponse response) {
        log.info("로그인 요청: {}", reqDto);
        TokenDTO tokenDto = authService.login(reqDto, response);
        log.info("로그인 성공: {}", tokenDto);
        return new SuccessResponse<>(tokenDto);
    }
}