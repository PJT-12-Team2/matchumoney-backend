package team2.pjt12.matchumoney.domain.auth.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "Auth API", description = "인증 및 로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @ApiOperation(
            value = "카카오 로그인",
            notes = "카카오 인가 코드를 받아 로그인/회원가입 처리 후 JWT 액세스 토큰을 반환합니다.")
    @PostMapping("/kakao-login")
    public SuccessResponse<LoginResponseDTO> kakaoLogin(@RequestBody SocialLoginRequestDTO request, HttpServletResponse response) {
        LoginResponseDTO resDto = authService.loginOrSignUp(request, response);
        return new SuccessResponse<>(resDto);
    }

    @ApiOperation(
            value = "회원가입",
            notes = "이메일 인증 완료 후, 닉네임/비밀번호 등으로 회원가입을 수행합니다."
    )
    @PostMapping("/signup")
    public SuccessResponse<?> signup(@ApiParam(value = "회원가입 요청 바디", required = true)
                                     @RequestBody @Valid SignupRequestDTO reqDto) {
        authService.signup(reqDto);
        return new SuccessResponse<>("회원가입 성공");
    }

    @ApiOperation(
            value = "로그인",
            notes = "자체 이메일/비밀번호 기반 로그인. 성공 시 JWT 토큰을 반환합니다."
    )
    @PostMapping("/login")
    public SuccessResponse<TokenDTO> signin(@ApiParam(value = "로그인 요청 바디", required = true)
                                            @RequestBody @Valid LoginRequestDTO reqDto,
                                            HttpServletResponse response) {
        log.info("로그인 요청: {}", reqDto);
        TokenDTO tokenDto = authService.login(reqDto, response);
        log.info("로그인 성공: {}", tokenDto);
        return new SuccessResponse<>(tokenDto);
    }

    @ApiOperation(
            value = "이메일 인증코드 전송(회원가입)",
            notes = "회원가입용 이메일로 인증코드를 발송합니다."
    )
    @PostMapping("/signup/email/send")
    public SuccessResponse<Boolean> sendSignupEmailVerification(@ApiParam(value = "이메일 인증코드 전송 요청 바디", required = true)
                                                                @RequestBody @Valid SendEmailRequestDTO reqDto) {
        return new SuccessResponse<>(authService.sendSignupEmailVerification(reqDto));
    }

    @ApiOperation(
            value = "이메일 인증코드 전송(비밀번호 재설정)",
            notes = "비밀번호 재설정용 인증코드를 이메일로 전송합니다."
    )
    @PostMapping("/reset/email/send")
    public SuccessResponse<Boolean> sendResetEmailVerification(@ApiParam(value = "이메일 인증코드 전송 요청 바디", required = true)
                                                               @RequestBody @Valid SendEmailRequestDTO reqDto) {
        return new SuccessResponse<>(authService.sendResetEmailVerification(reqDto));
    }

    @ApiOperation(
            value = "이메일 인증코드 검증",
            notes = "이메일로 전달된 인증코드를 검증합니다."
    )
    @PostMapping("/email/verify")
    public SuccessResponse<Boolean> verifyEmailCode(@ApiParam(value = "인증코드 검증 요청 바디", required = true)
                                                    @RequestBody @Valid VerifyEmailRequestDTO reqDto) {
        boolean verified = authService.verifyEmail(reqDto);
        return new SuccessResponse<>(verified);
    }

    @ApiOperation(
            value = "비밀번호 재설정",
            notes = "이메일 인증 완료 후 비밀번호를 재설정합니다."
    )
    @PatchMapping("/reset/password")
    public SuccessResponse<?> resetPassword(@ApiParam(value = "비밀번호 재설정 요청 바디", required = true)
                                            @RequestBody @Valid ResetRequestDTO reqDto) {
        authService.resetPassword(reqDto);
        return new SuccessResponse<>("비밀번호 재설정 성공");
    }

    @ApiOperation(
            value = "현재 비밀번호 검증",
            notes = "로그인 사용자의 비밀번호가 일치하는지 검증합니다."
    )
    // src/main/java/team2/pjt12/matchumoney/domain/auth/controller/AuthController.java
    @PostMapping("/verify/password")
    public ResponseEntity<?> verifyPassword(
            @AuthenticationPrincipal UserDetailsImpl user,
            @ApiParam(value = "비밀번호 검증 요청 바디", required = true)
            @RequestBody VerifyPasswordRequestDTO req) {

        authService.verifyPassword(user.getUser().getUserId(), req.getRawPassword());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @ApiOperation(
            value = "회원 탈퇴",
            notes = "현재 로그인된 사용자를 탈퇴 처리합니다."
    )
    @PostMapping("/withdraw")
    public SuccessResponse<?> withdraw(
            @RequestBody @Valid WithdrawRequestDTO reqDto
    ) {
        authService.withdraw(reqDto);
        return new SuccessResponse<>("회원 탈퇴 완료");
    }
}