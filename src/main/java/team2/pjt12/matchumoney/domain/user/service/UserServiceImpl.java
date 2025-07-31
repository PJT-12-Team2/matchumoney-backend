package team2.pjt12.matchumoney.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.dto.req.UpdatePasswordRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.req.UpdateUserInfoRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.MyPageResponseDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserResponseDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserUpdateResponseDTO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserUpdateResponseDTO updateUserInfo(UpdateUserInfoRequestDTO reqDto) {
        Long userId = getCurrentUser().getUserId();
        UserVO user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUserInfo(userId, reqDto.nickname, reqDto.gender, reqDto.birthDate);

        return new UserUpdateResponseDTO(userId);
    }

    @Override
    @Transactional
    public void updatePassword(UpdatePasswordRequestDTO reqDto) { // 소셜 로그인이 아닌 경우에만 사용
        Long userId = getCurrentUser().getUserId();
        UserVO user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(reqDto.currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호가 현재 비밀번호와 같은지 검사 (평문끼리)
        if (reqDto.newPassword.equals(reqDto.currentPassword)) {
            throw new CustomException(ErrorCode.SAME_PASSWORD);
        }

        if (!reqDto.newPassword.equals(reqDto.confirmPassword)) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        String encodedNewPassword = passwordEncoder.encode(reqDto.newPassword);
        userMapper.updatePassword(userId, encodedNewPassword);
    }

    //내 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getMyInfo() {
        UserVO user = getCurrentUser();
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return new UserResponseDTO(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getGender(),
                user.getBirthDate()
        );
    }
    @Override
    @Transactional
    public void updatePersona(String personaId) {
        Long userId = getCurrentUser().getUserId();
        UserVO user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        userMapper.updatePersona(userId, personaId);
    }

    @Override
    @Transactional(readOnly = true)
    public MyPageResponseDTO getMyPage() {
        Long userId = getCurrentUser().getUserId();
        UserVO user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<DepositProductResponseDTO> favoriteDeposits = userMapper.getFavoriteDeposits(userId);
        List<SavingListItemResponseDTO> favoriteSavings = userMapper.getFavoriteSavings(userId);
        List<CardSearchResponseDTO> favoriteCards = userMapper.getFavoriteCards(userId);

        return new MyPageResponseDTO(
                user.getNickname(),
                user.getPersonaId(),
                user.getExp(),
                favoriteDeposits,
                favoriteSavings,
                favoriteCards
        );
    }
}
