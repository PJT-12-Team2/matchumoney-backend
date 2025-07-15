package team2.pjt12.matchumoney.domain.auth.service;

import team2.pjt12.matchumoney.domain.auth.dto.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.LoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.SendEmailRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.SignupRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.VerifyEmailRequestDTO;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    LoginResponseDTO loginOrSignUp(SocialLoginRequestDTO request);

    void signup(SignupRequestDTO reqDto);

    TokenDTO login(LoginRequestDTO reqDto, HttpServletResponse response);

    boolean sendSignupEmailVerification(SendEmailRequestDTO reqDto);

    boolean sendResetEmailVerification(SendEmailRequestDTO reqDto);

    boolean verifyEmail(VerifyEmailRequestDTO reqDto);

}