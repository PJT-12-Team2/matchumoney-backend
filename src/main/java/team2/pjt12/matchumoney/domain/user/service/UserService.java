package team2.pjt12.matchumoney.domain.user.service;

import team2.pjt12.matchumoney.domain.auth.dto.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.LoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.SignupRequestDTO;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    void signup(SignupRequestDTO reqDto);

    TokenDTO login(LoginRequestDTO reqDto, HttpServletResponse response);
}
