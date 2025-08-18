package team2.pjt12.matchumoney.domain.auth.service;

import team2.pjt12.matchumoney.domain.auth.dto.res.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.res.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.*;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    LoginResponseDTO loginOrSignUp(SocialLoginRequestDTO request, HttpServletResponse response);

    void signup(SignupRequestDTO reqDto);

    TokenDTO login(LoginRequestDTO reqDto, HttpServletResponse response);

    boolean sendSignupEmailVerification(SendEmailRequestDTO reqDto);

    boolean sendResetEmailVerification(SendEmailRequestDTO reqDto);

    boolean verifyEmail(VerifyEmailRequestDTO reqDto);

    void resetPassword(ResetRequestDTO reqDto);

    void verifyPassword(Long userId, String rawPassword);

    void withdraw(WithdrawRequestDTO req);
}