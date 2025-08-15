package team2.pjt12.matchumoney.domain.auth.service;

import team2.pjt12.matchumoney.domain.auth.dto.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.*;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    LoginResponseDTO loginOrSignUp(SocialLoginRequestDTO request);

    void signup(SignupRequestDTO reqDto);

    TokenDTO login(LoginRequestDTO reqDto, HttpServletResponse response);

    boolean sendSignupEmailVerification(SendEmailRequestDTO reqDto);

    boolean sendResetEmailVerification(SendEmailRequestDTO reqDto);

    boolean verifyEmail(VerifyEmailRequestDTO reqDto);

    void resetPassword(ResetRequestDTO reqDto);

    // src/main/java/team2/pjt12/matchumoney/domain/auth/service/AuthService.java
    boolean verifyCurrentPassword(String rawPassword);


}