// src/test/java/team2/pjt12/matchumoney/domain/auth/service/AuthServiceImplTest.java
package team2.pjt12.matchumoney.domain.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import team2.pjt12.matchumoney.domain.auth.client.KakaoApiClient;
import team2.pjt12.matchumoney.domain.auth.dto.req.*;
import team2.pjt12.matchumoney.domain.auth.dto.res.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.res.SocialUserInfo;
import team2.pjt12.matchumoney.domain.auth.dto.res.TokenDTO;
import team2.pjt12.matchumoney.domain.auth.mapper.AuthMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.email.EmailService;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;
import team2.pjt12.matchumoney.global.jwt.JwtServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock KakaoApiClient kakaoApiClient;
    @Mock AuthMapper authMapper;
    @Mock UserMapper userMapper;
    @Mock JwtServiceImpl jwtService;
    @Mock RedisTemplate<String, String> redisTemplate;
    @Mock ValueOperations<String, String> valueOps;
    @Mock org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock EmailService emailService;
    @Mock HttpServletResponse response;

    @InjectMocks AuthServiceImpl service;

    private UserVO user(long id, String email, String encPw) {
        UserVO u = new UserVO();
        u.setUserId(id);
        u.setEmail(email);
        u.setPassword(encPw);
        u.setNickname("nick"+id);
        return u;
    }

    // 공통 Redis ops 세팅
    private void mockValueOps() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    // -------------------- 소셜 로그인 --------------------
    @Test @DisplayName("AU-001 소셜 로그인 성공 (기존 사용자)")
    void AU001() {
        SocialLoginRequestDTO req = new SocialLoginRequestDTO();
        req.setCode("validCode");

        SocialUserInfo info = SocialUserInfo.builder()
                .socialId("SID-1").email("a@test.com").nickname("A").profileImageUrl("img").build();

        given(kakaoApiClient.getUserInfoByCode("validCode")).willReturn(info);
        given(userMapper.findBySocialIdAndSocialProvider("SID-1","KAKAO"))
                .willReturn(Optional.of(user(10L,"a@test.com","ENC")));
        given(jwtService.createAccessToken(any(UserVO.class))).willReturn("AT");
        given(jwtService.createRefreshToken(any(UserVO.class))).willReturn("RT");
        mockValueOps();
        // refresh 저장 TTL
        given(jwtService.getExpiration("RT")).willReturn(3600_000L);

        LoginResponseDTO res = service.loginOrSignUp(req, response);

        assertEquals("AT", res.getAccessToken());
        verify(valueOps).set(eq("refresh:a@test.com"), eq("RT"), eq(3600_000L), eq(TimeUnit.MILLISECONDS));
        verify(jwtService).registerTokens(eq(10L), eq("AT"), eq("RT"));
    }

    @Test @DisplayName("AU-002 소셜 로그인 성공 (신규 사용자)")
    void AU002() {
        SocialLoginRequestDTO req = new SocialLoginRequestDTO();
        req.setCode("newCode");

        SocialUserInfo info = SocialUserInfo.builder()
                .socialId("SID-2").email("b@test.com").nickname("B").profileImageUrl("img").build();

        given(kakaoApiClient.getUserInfoByCode("newCode")).willReturn(info);
        // 신규 → 저장 후 곧바로 반환 객체 사용 (registerUser 경로)
        given(userMapper.findBySocialIdAndSocialProvider("SID-2","KAKAO")).willReturn(Optional.empty());
        // save 시 id 할당하도록 흉내
        doAnswer(inv -> { UserVO u = inv.getArgument(0); u.setUserId(20L); return null; })
                .when(authMapper).save(any(UserVO.class));

        given(jwtService.createAccessToken(any(UserVO.class))).willReturn("AT2");
        given(jwtService.createRefreshToken(any(UserVO.class))).willReturn("RT2");
        mockValueOps();
        given(jwtService.getExpiration("RT2")).willReturn(7200_000L);

        LoginResponseDTO res = service.loginOrSignUp(req, response);

        assertEquals("AT2", res.getAccessToken());
        verify(authMapper).save(any(UserVO.class));
        verify(valueOps).set(eq("refresh:b@test.com"), eq("RT2"), eq(7200_000L), eq(TimeUnit.MILLISECONDS));
        verify(jwtService).registerTokens(eq(20L), eq("AT2"), eq("RT2"));
    }

    // -------------------- 회원가입 --------------------





    // -------------------- 이메일 인증번호 발송 --------------------
    @Test @DisplayName("AU-009 이메일 인증번호 발송 성공 (회원가입)")
    void AU009() {
        when(userMapper.isExistsByEmail("i@test.com")).thenReturn(false);
        when(emailService.sendEmail(eq("i@test.com"), anyString(), anyString())).thenReturn(true);
        mockValueOps();

        boolean ok = service.sendSignupEmailVerification(new SendEmailRequestDTO("i@test.com"));
        assertTrue(ok);

        // 코드 저장 확인
        verify(redisTemplate).delete(eq("email:verification:i@test.com"));
        verify(valueOps).set(eq("email:verification:i@test.com"), anyString(), eq(5L), eq(TimeUnit.MINUTES));
        verify(emailService).sendEmail(eq("i@test.com"), anyString(), anyString());
    }

    @Test @DisplayName("AU-010 이메일 인증번호 발송 실패 (이미 가입된 메일)")
    void AU010() {
        when(userMapper.isExistsByEmail("j@test.com")).thenReturn(true);
        CustomException ex = assertThrows(CustomException.class,
                () -> service.sendSignupEmailVerification(new SendEmailRequestDTO("j@test.com")));
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, ex.getErrorCode());
    }

    // -------------------- 이메일 인증 검증 --------------------
    @Test @DisplayName("AU-011 이메일 인증 검증 성공")
    void AU011() {
        mockValueOps();
        when(valueOps.get("email:verification:k@test.com")).thenReturn("123456");

        boolean ok = service.verifyEmail(new VerifyEmailRequestDTO("k@test.com","123456"));
        assertTrue(ok);

        verify(valueOps).set(eq("email:verified:k@test.com"), eq("true"), eq(Duration.ofMinutes(30)));
        verify(redisTemplate).delete("email:verification:k@test.com");
    }

    @Test @DisplayName("AU-012 이메일 인증 검증 실패 (코드 불일치/없음)")
    void AU012() {
        mockValueOps();
        when(valueOps.get("email:verification:l@test.com")).thenReturn("654321");

        boolean ok = service.verifyEmail(new VerifyEmailRequestDTO("l@test.com","wrong"));
        assertFalse(ok);
        verify(valueOps, never()).set(eq("email:verified:l@test.com"), anyString(), any(Duration.class));
        verify(redisTemplate, never()).delete("email:verification:l@test.com");
    }



    @Test @DisplayName("AU-017 비밀번호 검증 실패 (빈 입력)")
    void AU017() {
        assertThrows(BadCredentialsException.class, () -> service.verifyPassword(77L,""));
    }

    @Test @DisplayName("AU-019 비밀번호 검증 실패 (사용자 없음)")
    void AU019() {
        when(userMapper.findByUserId(999L)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> service.verifyPassword(999L,"abcd"));
    }
}
