package team2.pjt12.matchumoney.domain.auth.client;

import team2.pjt12.matchumoney.domain.auth.dto.res.SocialUserInfo;

public interface SocialApiClient {
    SocialUserInfo getUserInfoByCode(String code);
}
