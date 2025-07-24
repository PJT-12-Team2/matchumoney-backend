package team2.pjt12.matchumoney.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;

@Slf4j
@Configuration
public class SecurityUtils {
    public static UserVO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current User: {}", authentication.getName());
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED); // 인증되지 않은 사용자
        }

        Object principal = authentication.getPrincipal();
        // 예외 방지: principal이 User 경우에만 반환
        if (principal instanceof UserVO) {
            return (UserVO) principal;
        }

        // principal이 UserDetailsImpl 같은 커스텀 클래스인 경우
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return userDetails.getUser();
        }

        // 그 외에는 예외 처리
        throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
    }
}
