package team2.pjt12.matchumoney.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
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

    @Override
    public LoginResponseDTO loginOrSignUp(SocialLoginRequestDTO request) {
        SocialUserInfo userInfo = kakaoApiClient.getUserInfoByCode(request.getCode());

        UserVO user = userMapper.findBySocialIdAndSocialProvider(userInfo.getSocialId(), "KAKAO")
                .orElseGet(() -> registerUser(userInfo));

        String jwt = jwtService.createAccessToken(user);

        return LoginResponseDTO.builder()
                .accessToken(jwt)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .personaId(user.getPersonaId())
                .build();
    }

    private UserVO registerUser(SocialUserInfo info) {
        UserVO user = UserVO.builder()
                .socialProvider("KAKAO")
                .socialId(info.getSocialId())
                .email(info.getEmail())
                .password(passwordEncoder.encode(info.getSocialId())) // 임시 비밀번호로 소셜 ID 사용
                .nickname(info.getNickname())
                .profileImageUrl(info.getProfileImageUrl())
                .socialLogin(true)
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
                reqDto.getEmail(),
                encodedPassword,
                reqDto.getNickname(),
                reqDto.getProfileImageUrl(),
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
                .personaId(user.getPersonaId())
                .build();
    }

    //인증번호 전송
    @Override
    public boolean sendSignupEmailVerification(SendEmailRequestDTO reqDto) {
        if (userMapper.isExistsByEmail(reqDto.getEmail())) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }
        return sendEmailCode(reqDto.getEmail());
    }

    @Override
    public boolean sendResetEmailVerification(SendEmailRequestDTO reqDto) {
        if (!userMapper.isExistsByEmail(reqDto.getEmail())) {
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

        authMapper.updatePassword(reqDto.getEmail(), passwordEncoder.encode(reqDto.getNewPassword()));
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyPassword(Long userId, String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new BadCredentialsException("비밀번호가 비어 있습니다.");
        }

        UserVO user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + userId));

        boolean ok = passwordEncoder.matches(rawPassword, user.getPassword());

        // 민감정보 노출 주의: 원문 비번은 절대 로그에 찍지 않음
        log.info("verifyPassword: uid={}, rawLen={}, hashPrefix={}, matches={}",
                userId,
                rawPassword.length(),
                user.getPassword() == null ? null : user.getPassword().substring(0, 7), // ex) $2a$10
                ok
        );

        if (!ok) {
            throw new BadCredentialsException("비밀번호 불일치");
        }
    }
}
