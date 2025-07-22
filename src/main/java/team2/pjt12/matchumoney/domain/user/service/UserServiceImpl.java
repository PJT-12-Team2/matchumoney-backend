package team2.pjt12.matchumoney.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.dto.req.UserUpdateRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserUpdateResponseDTO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private UserVO getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    @Override
    @Transactional
    public UserUpdateResponseDTO updateUserInfo(UserUpdateRequestDTO reqDto) {
        Long userId = getCurrentUser().getUserId();
        UserVO user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateUserInfo(reqDto.gender, reqDto.birthDate);

        return new UserUpdateResponseDTO(userId);
    }

}
