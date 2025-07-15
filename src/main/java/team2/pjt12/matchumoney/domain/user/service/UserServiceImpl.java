package team2.pjt12.matchumoney.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.auth.dto.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.LoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.SignupRequestDTO;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;
import team2.pjt12.matchumoney.global.jwt.JwtServiceImpl;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtServiceImpl jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    private UserVO getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    @Override
    public void signup(SignupRequestDTO reqDto) {

        String verifiedKey = "email:verified:" + reqDto.getEmail();
        String isVerified = redisTemplate.opsForValue().get(verifiedKey);
        if (!"true".equals(isVerified)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (userMapper.findByEmail(reqDto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_AVAILABLE);
        }

        if (!reqDto.getPassword().equals(reqDto.getPasswordCheck())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(reqDto.getPassword());

        UserVO user = new UserVO(
                null,
                null,
                null,
                reqDto.getEmail(),
                encodedPassword,
                reqDto.getNickname(),
                reqDto.getProfileImageUrl(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                false
        );

        userMapper.save(user);

        redisTemplate.delete(verifiedKey);
    }

    //로그인
    @Override
    public TokenDTO login(LoginRequestDTO reqDto, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(reqDto.getEmail(), reqDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);
        String email = authentication.getName();

        UserVO user = userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, jwtService.getExpiration(refreshToken), TimeUnit.MILLISECONDS);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        return TokenDTO.builder()
                .accessToken(accessToken)
                .userId(user.getId())
                .nickname(user.getNickname())
                .build();
    }

}
