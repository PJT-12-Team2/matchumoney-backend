package team2.pjt12.matchumoney.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.auth.client.KakaoApiClient;
import team2.pjt12.matchumoney.domain.auth.dto.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialUserInfo;
import team2.pjt12.matchumoney.domain.auth.dto.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.dto.req.*;
import team2.pjt12.matchumoney.domain.auth.mapper.AuthMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.email.EmailService;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;
import team2.pjt12.matchumoney.global.jwt.JwtServiceImpl;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final KakaoApiClient kakaoApiClient;
    private final AuthMapper authMapper;
    private final UserMapper userMapper;
    private final JwtServiceImpl jwtService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    private UserVO getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    @Override
    public LoginResponseDTO loginOrSignUp(SocialLoginRequestDTO request) {
        SocialUserInfo userInfo = kakaoApiClient.getUserInfoByCode(request.getCode());

        UserVO user = userMapper.findBySocialIdAndSocialProvider(userInfo.getSocialId(), "KAKAO")
                .orElseGet(() -> registerUser(userInfo));

        String jwt = jwtService.createAccessToken(user);

        return new LoginResponseDTO(jwt);
    }

    private UserVO registerUser(SocialUserInfo info) {
        UserVO user = UserVO.builder()
                .id(null)
                .socialProvider("KAKAO")
                .socialId(info.getSocialId())
                .email(info.getEmail())
                .password(passwordEncoder.encode(info.getSocialId())) // 임시 비밀번호로 소셜 ID 사용
                .nickname(info.getNickname())
                .profileImageUrl(info.getProfileImageUrl())
                .createdTime(LocalDateTime.now())
                .lastModifiedTime(LocalDateTime.now())
                .socialLogin(true)
                .personaId(null)
                .productId(null)
                .exp(0)
                .build();

        authMapper.save(user);
        return user;
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
                false,
                null,
                null,
                0
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
        UserVO user = userMapper.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        redisTemplate.opsForValue().set("refresh:" + email, refreshToken, jwtService.getExpiration(refreshToken), TimeUnit.MILLISECONDS);
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        return TokenDTO.builder()
                .accessToken(accessToken)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }

    //인증번호 전송
    @Override
    public boolean sendSignupEmailVerification(SendEmailRequestDTO reqDto) {
        if (userMapper.existsByEmail(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
        }
        return sendEmailCode(reqDto.getEmail());
    }

    @Override
    public boolean sendResetEmailVerification(SendEmailRequestDTO reqDto) {
        if (!userMapper.existsByEmail(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        return sendEmailCode(reqDto.getEmail());
    }

    private boolean sendEmailCode(String email) {
        String redisKey = "email:verification:" + email;

        redisTemplate.delete(redisKey);
        String code = String.format("%06d", new Random().nextInt(1000000));
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        return emailService.sendEmail(email, "이메일 인증번호", "인증번호는 " + code + " 입니다.");
    }

    @Override
    @Transactional
    public boolean verifyEmail(VerifyEmailRequestDTO reqDto) {
        String emailKey = "email:verification:" + reqDto.getEmail();
        String storedCode = redisTemplate.opsForValue().get(emailKey);

        if (storedCode == null || !storedCode.equals(reqDto.getCode())) {
            log.info("storeCode: {}, reqCode: {}", storedCode, reqDto.getCode());
            return false;
        }

        String verifiedKey = "email:verified:" + reqDto.getEmail();
        redisTemplate.opsForValue().set(verifiedKey, "true", Duration.ofMinutes(30));


        redisTemplate.delete(emailKey);
        return true;
    }

    @Override
    public void resetPassword(ResetRequestDTO reqDto) {
        if (!reqDto.getNewPassword().equals(reqDto.getConfirmPassword())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }

        UserVO user = userMapper.findByEmail(reqDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        authMapper.updatePassword(passwordEncoder.encode(reqDto.getNewPassword()));
    }
}
