package team2.pjt12.matchumoney.global.jwt;

import team2.pjt12.matchumoney.domain.user.domain.UserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface JwtService {

    String createAccessToken(UserVO user);

    String createRefreshToken(UserVO user);

    Optional<String> extractAccessToken(HttpServletRequest request);

    Optional<String> extractRefreshToken(HttpServletRequest request);

    Optional<String> extractEmail(String accessToken);

    boolean isTokenValid(String token);

    long getExpiration(String token);

    void sendAccessToken(HttpServletResponse response, String newAccessToken);

    void sendAccessAndRefreshToken(HttpServletResponse response,
                                   String accessToken,
                                   String refreshToken);
}
