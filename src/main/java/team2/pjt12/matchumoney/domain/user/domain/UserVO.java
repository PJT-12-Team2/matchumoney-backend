package team2.pjt12.matchumoney.domain.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
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
    private Long favoriteId;
    private Long productId;
    private Integer exp;
    private Gender gender;
    private LocalDate birthDate;

    @Builder
    public UserVO(Long userId, String socialProvider, String socialId, String email,
                  String password, String nickname, String profileImageUrl,
                  LocalDateTime createdTime, LocalDateTime lastModifiedTime,
                  Boolean socialLogin, Long personaId, Long favoriteId, Long productId,
                  Integer exp, Gender gender, LocalDate birthDate) {
        this.userId = userId;
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
        this.favoriteId = favoriteId;
        this.productId = productId;
        this.exp = exp;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public UserVO(String socialProvider, String socialId, String email,
                  String password, String nickname, String profileImageUrl, boolean socialLogin) {
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.isSocialLogin = socialLogin;
    }
}