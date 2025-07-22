package team2.pjt12.matchumoney.global.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtLogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 액세스 토큰 추출
        jwtService.extractAccessToken(request).ifPresent(token -> {
            if (jwtService.isTokenValid(token)) {
                long expirationTime = jwtService.getExpiration(token); // 토큰의 만료 시간
                long currentTime = System.currentTimeMillis();
                long ttl = expirationTime - currentTime;
                if (ttl > 0) {
                    String redisKey = "blacklist:" + token;
                    redisTemplate.opsForValue().set(redisKey, "true", ttl, TimeUnit.MILLISECONDS);
                    log.info("토큰 {}이 로그아웃되어 블랙리스트에 등록되었습니다. (TTL: {}ms)", token, ttl);
                } else {
                    log.info("토큰 {}은 이미 만료되어 블랙리스트 등록이 필요하지 않습니다.", token);
                }
            } else {
                log.warn("유효하지 않은 토큰입니다: {}", token);
            }
        });

        // 리프레시 토큰 추출 및 삭제 처리
        jwtService.extractRefreshToken(request).ifPresent(refreshToken -> {
            jwtService.extractEmail(refreshToken).ifPresent(email -> {
                String redisKey = "refresh:" + email;
                redisTemplate.delete(redisKey);
                log.info("이메일 {}에 해당하는 리프레시 토큰이 Redis에서 삭제되었습니다.", email);
            });
        });
    }
}
