package team2.pjt12.matchumoney.global.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret; // Base64로 인코딩된 시크릿 키 문자열

    @Value("${jwt.access.expiration}")
    private long accessTokenExpireTime;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpireTime;

    @Value("${jwt.access.header}")
    private String accessTokenHeader;

    private Key secretKey;

    @PostConstruct
    public void init() {
        // jwtSecret을 바탕으로 암호화에 사용할 Key 객체 생성
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(UserVO user) {
        String email = user.getEmail();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpireTime);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String createRefreshToken(UserVO user) {
        String email = user.getEmail();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpireTime);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        String header = request.getHeader(accessTokenHeader);
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring("Bearer ".length()));
        }
        return Optional.empty();
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<String> extractEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    @Override
    public long getExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().getTime();
        } catch (JwtException e) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response,
                                          String accessToken,
                                          String refreshToken) {
        response.setHeader("Authorization", "Bearer " + accessToken);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // 추후 true로 변경 필요 (HTTPS 환경에서만)
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (refreshTokenExpireTime / 1000)); // 초 단위로 설정

        response.addCookie(refreshCookie);
    }

    @Override
    public void sendAccessToken(HttpServletResponse response, String newAccessToken) {
        response.setHeader(accessTokenHeader, "Bearer " + newAccessToken);
    }

    @Override
    public Optional<Long> getUserIdFromToken(HttpServletRequest request) {
        return extractAccessToken(request)
                .filter(this::isTokenValid)
                .map(token -> {
                    try {
                        Claims claims = Jwts.parserBuilder()
                                .setSigningKey(secretKey) // ✅ 하드코딩 말고 필드 사용
                                .build()
                                .parseClaimsJws(token)
                                .getBody();
                        return Long.valueOf(claims.get("userId").toString());
                    } catch (Exception e) {
                        return null;
                    }
                });
    }
}