package team2.pjt12.matchumoney.domain.auth.dto;

import lombok.Getter;

@Getter
public class SocialUserInfo {

    private final String socialId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    public SocialUserInfo(String socialId, String email, String nickname, String profileImageUrl) {
        this.socialId = socialId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}