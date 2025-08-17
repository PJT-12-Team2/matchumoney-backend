package team2.pjt12.matchumoney.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.auth.dto.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.*;
import team2.pjt12.matchumoney.domain.auth.service.AuthService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
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

    // 인증번호 전송
    @PostMapping("/signup/email/send")
    public SuccessResponse<Boolean> sendSignupEmailVerification(@RequestBody @Valid SendEmailRequestDTO reqDto) {
        return new SuccessResponse<>(authService.sendSignupEmailVerification(reqDto));
    }

    @PostMapping("/reset/email/send")
    public SuccessResponse<Boolean> sendResetEmailVerification(@RequestBody @Valid SendEmailRequestDTO reqDto) {
        return new SuccessResponse<>(authService.sendResetEmailVerification(reqDto));
    }

    // 인증번호 검증
    @PostMapping("/email/verify")
    public SuccessResponse<Boolean> verifyEmailCode(@RequestBody @Valid VerifyEmailRequestDTO reqDto) {
        boolean verified = authService.verifyEmail(reqDto);
        return new SuccessResponse<>(verified);
    }

    @PatchMapping("/reset/password")
    public SuccessResponse<?> resetPassword(@RequestBody @Valid ResetRequestDTO reqDto) {
        authService.resetPassword(reqDto);
        return new SuccessResponse<>("비밀번호 재설정 성공");
    }
    // src/main/java/team2/pjt12/matchumoney/domain/auth/controller/AuthController.java
    @PostMapping("/verify/password")
    public SuccessResponse<Boolean> verifyPassword(
            @RequestBody @Valid VerifyPasswordRequestDTO reqDto) {
        boolean valid = authService.verifyCurrentPassword(reqDto.getPassword());
        return new SuccessResponse<>(valid);
    }

}