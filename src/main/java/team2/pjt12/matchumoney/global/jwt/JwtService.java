package team2.pjt12.matchumoney.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String rawSecretKey; // Base64로 인코딩된 시크릿 키 문자열

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    private Key getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(rawSecretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    public String issueToken(UserVO user) {
        System.out.println("user.getId() = " + user.getId());
        System.out.println("user.getNickname() = " + user.getNickname());
        System.out.println("user.getSocialProvider() = " + user.getSocialProvider());
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("nickname", user.getNickname())
                .claim("provider", user.getSocialProvider())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}