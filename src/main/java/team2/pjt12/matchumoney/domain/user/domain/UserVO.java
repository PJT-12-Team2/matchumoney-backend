package team2.pjt12.matchumoney.domain.user.domain;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class UserVO {

    private Long userId;
    private String socialProvider;
    private String socialId;
    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;
    private LocalDateTime createdTime;
    private LocalDateTime lastModifiedTime;
    private Boolean isSocialLogin;
    private Long personaId;
    private Long productId;
    private Integer exp;
    private Long finId;

    @Builder
    public UserVO(Long id, String socialProvider, String socialId, String email,
                  String password, String nickname, String profileImageUrl,
                  LocalDateTime createdTime, LocalDateTime lastModifiedTime, boolean socialLogin,
                  Long personaId, Long productId, Integer exp) {
        this.userId = id;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.createdTime = createdTime;
        this.lastModifiedTime = lastModifiedTime;
        this.isSocialLogin = socialLogin;
        this.personaId = personaId;
        this.productId = productId;
        this.exp = exp;
    }
}