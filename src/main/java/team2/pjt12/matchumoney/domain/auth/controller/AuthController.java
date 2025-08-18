package team2.pjt12.matchumoney.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.auth.dto.res.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.res.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.*;
import team2.pjt12.matchumoney.domain.auth.service.AuthService;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao-login")
    public SuccessResponse<LoginResponseDTO> kakaoLogin(@RequestBody SocialLoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO resDto = authService.loginOrSignUp(request, response);
        return new SuccessResponse<>(resDto);
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
    public ResponseEntity<?> verifyPassword(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody VerifyPasswordRequestDTO req) {

        authService.verifyPassword(user.getUser().getUserId(), req.getRawPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/withdraw")
    public SuccessResponse<?> withdraw(
            @RequestBody @Valid WithdrawRequestDTO reqDto
    ) {
        authService.withdraw(reqDto);
        return new SuccessResponse<>("회원 탈퇴 완료");
    }
}