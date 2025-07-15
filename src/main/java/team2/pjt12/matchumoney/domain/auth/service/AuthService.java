package team2.pjt12.matchumoney.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.auth.client.KakaoApiClient;
import team2.pjt12.matchumoney.domain.auth.dto.LoginResponseDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginRequestDTO;
import team2.pjt12.matchumoney.domain.auth.dto.SocialUserInfo;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.jwt.JwtServiceImpl;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final UserMapper userMapper;
    private final JwtServiceImpl jwtService;

    public LoginResponseDTO loginOrSignUp(SocialLoginRequestDTO request) {
        SocialUserInfo userInfo = kakaoApiClient.getUserInfoByCode(request.getCode());

        UserVO user = userMapper.findBySocialIdAndSocialProvider(userInfo.getSocialId(), "KAKAO")
                .orElseGet(() -> registerUser(userInfo));

        String jwt = jwtService.createAccessToken(user);

        return new LoginResponseDTO(jwt);
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
                .socialLogin(true)
                .build();

        userMapper.save(user);
        return user;
    }
}