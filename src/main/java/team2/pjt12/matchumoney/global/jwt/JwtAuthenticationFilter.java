package team2.pjt12.matchumoney.global.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // ✅ 실제 API 경로에 맞춤
    private static final String SOCIAL_LOGIN_URL = "/api/auth/kakao-login";
    private static final String LOGIN_URL        = "/api/auth/login";
    private static final String LOGOUT_URL       = "/api/auth/logout";

    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserMapper userMapper;

    /**
     * ✅ 반드시 "정확히" 공개해야 하는 엔드포인트만 허용합니다.
     *    아래에 없는 /api/auth/** 는 모두 인증 필요!
     */
    private boolean isPermitAllRequest(HttpServletRequest req) {
        final String uri = req.getRequestURI();
        final String method = req.getMethod();

        // 1) CORS preflight는 항상 통과
        if ("OPTIONS".equalsIgnoreCase(method)) return true;

        // 2) 문서/정적 리소스
        if (uri.startsWith("/swagger-ui")
                || uri.equals("/swagger-ui.html")
                || uri.startsWith("/v2/api-docs")
                || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/webjars")
                || uri.startsWith("/static/")) {
            return true;
        }

        // 3) OAuth 콜백 등
        if (uri.startsWith("/oauth/")) return true;

        // 4) 공개 Auth 엔드포인트(정확히 나열)
        if (uri.equals(LOGIN_URL)
                || uri.equals(SOCIAL_LOGIN_URL)
                || uri.equals("/api/auth/signup")
                || uri.equals("/api/auth/signup/email/send")
                || uri.equals("/api/auth/email/verify")
                || uri.startsWith("/api/auth/reset/")) { // 비번 재설정(이메일 전송/검증/실행 등)
            return true;
        }

        // ❌ 절대 여기 넣지 말 것:
        // - /api/auth/verify/password (비번 검증은 인증 필요)
        // - /api/user/**, /api/users/me/** 등 민감 API

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();

        // ✅ 예외(공개) URI는 바로 통과
        if (isPermitAllRequest(request)) {
            log.info("JwtAuthenticationFilter: 예외 URI {} → 필터 통과", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JwtAuthenticationFilter 시작: {}", requestURI);

        // (중복이지만 안전장치로 유지해도 무방)
        if (requestURI.equals(SOCIAL_LOGIN_URL) || requestURI.equals(LOGIN_URL) || requestURI.equals(LOGOUT_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        // ====== 토큰 추출/검증 로그 ======
        final String rawAuthHeader = request.getHeader("Authorization");
        log.info("Authorization Header: {}", rawAuthHeader);

        final String extractedToken = jwtService.extractAccessToken(request).orElse("null");
        log.info("🔍 추출된 AccessToken(검사용): {}", extractedToken);

        final boolean isValid = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .isPresent();
        log.info("✅ isTokenValid 결과: {}", isValid);
        // =============================

        // 리프레시 토큰 (쿠키/헤더 등)
        final String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        // 액세스 토큰
        final String accessToken = jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if (accessToken == null) {
            log.warn("❌ 액세스 토큰이 null입니다. Authorization 헤더 확인 필요");
        }

        log.info("Request URI: {}", requestURI);
        log.info("AccessToken: {}", accessToken);
        log.info("RefreshToken: {}", refreshToken);

        // 액세스+리프레시 모두 있는 경우 (정상 시나리오)
        if (accessToken != null && refreshToken != null) {
            // 로그아웃 블랙리스트 체크
            if (redisTemplate.opsForValue().get("blacklist:" + accessToken) != null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("this token is expired token");
                return;
            }
            authenticateUser(accessToken);
            filterChain.doFilter(request, response);
            return;
        }

        // 액세스는 없고 리프레시만 있는 경우: 액세스 재발급
        if (accessToken == null && refreshToken != null) {
            final String newAccessToken = reIssueAccessToken(refreshToken);
            jwtService.sendAccessToken(response, newAccessToken);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Access token re-issued. Please retry with new token.");
            return;
        }

        // 액세스만 있는 경우: 인증 세팅
        if (accessToken != null) {
            // 로그아웃 블랙리스트 체크 (필요시 활성화)
//            if (redisTemplate.opsForValue().get("blacklist:" + accessToken) != null) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("this token is expired token");
//                return;
//            }
            authenticateUser(accessToken);
        }

        filterChain.doFilter(request, response);
    }

    // 액세스 토큰으로 사용자 인증 처리
    private void authenticateUser(String accessToken) {
        jwtService.extractEmail(accessToken).ifPresent(
                email -> userMapper.findByEmail(email).ifPresent(user -> {
                    UserDetailsImpl userDetails = new UserDetailsImpl(user);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                })
        );
    }

    // 리프레시 토큰으로 새로운 액세스 토큰 발급
    private String reIssueAccessToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("RefreshToken에서 이메일 추출 실패"));

        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + email);
        if (refreshToken.equals(storedRefreshToken)) {
            UserVO user = userMapper.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 회원을 찾을 수 없습니다."));
            String newAccessToken = jwtService.createAccessToken(user);
            log.info("AccessToken 재발급: {}", newAccessToken);
            return newAccessToken;
        }
        throw new IllegalArgumentException("유효하지 않은 refresh token");
    }
}
