package team2.pjt12.matchumoney.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.auth.client.KakaoApiClient;
import team2.pjt12.matchumoney.domain.auth.dto.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialUserInfo;
import team2.pjt12.matchumoney.domain.auth.dto.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.LoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.SignupRequestDTO;
import team2.pjt12.matchumoney.domain.auth.mapper.AuthMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;
import team2.pjt12.matchumoney.global.jwt.JwtServiceImpl;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final KakaoApiClient kakaoApiClient;
    private final AuthMapper authMapper;
    private final JwtServiceImpl jwtService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private UserVO getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }
    private UserVO registerUser(SocialUserInfo info) {
        UserVO user = UserVO.builder()
                .id(null)
                .socialProvider("KAKAO")
                .socialId(info.getSocialId())
                .email(info.getEmail())
                .nickname(info.getNickname())
                .profileImageUrl(info.getProfileImageUrl())
                .createdTime(LocalDateTime.now())
                .lastModifiedTime(LocalDateTime.now())
                .socialLogin(true)
                .build();

        authMapper.save(user);
        return user;
    }

    @Override
    public LoginResponseDTO loginOrSignUp(SocialLoginRequestDTO request) {
        SocialUserInfo userInfo = kakaoApiClient.getUserInfoByCode(request.getCode());

        UserVO user = authMapper.findBySocialIdAndSocialProvider(userInfo.getSocialId(), "KAKAO")
                .orElseGet(() -> registerUser(userInfo));

        String jwt = jwtService.createAccessToken(user);

        return new LoginResponseDTO(jwt);
    }

    @Override
    public void signup(SignupRequestDTO reqDto) {
        String verifiedKey = "email:verified:" + reqDto.getEmail();
        String isVerified = redisTemplate.opsForValue().get(verifiedKey);
        if (!"true".equals(isVerified)) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (authMapper.findByEmail(reqDto.getEmail()).isPresent()) {
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

        authMapper.save(user);

        redisTemplate.delete(verifiedKey);
    }

    //로그인
    @Override
    public TokenDTO login(LoginRequestDTO reqDto, HttpServletResponse response) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(reqDto.getEmail(), reqDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);
        String email = authentication.getName();

        UserVO user = authMapper.findByEmail(email)
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
