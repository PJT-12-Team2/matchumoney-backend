package team2.pjt12.matchumoney.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.auth.client.KakaoApiClient;
import team2.pjt12.matchumoney.domain.auth.dto.LoginResDto;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginReqDto;
import team2.pjt12.matchumoney.domain.auth.dto.SocialUserInfo;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.jwt.JwtService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    public LoginResDto loginOrSignUp(SocialLoginReqDto request) {
        SocialUserInfo userInfo = kakaoApiClient.getUserInfoByCode(request.getCode());

        UserVO user = userMapper.findBySocialIdAndSocialProvider(userInfo.getSocialId(), "KAKAO")
                .orElseGet(() -> registerUser(userInfo));

        String jwt = jwtService.issueToken(user);
        boolean isNewUser = user.getCreatedTime().isAfter(LocalDateTime.now().minusMinutes(1));

        return new LoginResDto(jwt, isNewUser);
    }

    private UserVO registerUser(SocialUserInfo info) {
        UserVO user = UserVO.builder()
                .id(null)
                .socialProvider("KAKAO")
                .socialId(info.getSocialId())
                .email(info.getEmail())
                .nickname(info.getNickname())
                .profileImageUrl(info.getProfileImageUrl())
                .createdTime(LocalDateTime.now())
                .lastModifiedTime(LocalDateTime.now())
                .build();

        userMapper.save(user);
        return user;
    }
}