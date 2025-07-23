package team2.pjt12.matchumoney.domain.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.user.domain.Gender;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;

import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<UserVO> findBySocialIdAndSocialProvider(@Param("socialId") String socialId, @Param("socialProvider") String socialProvider);

    Optional<UserVO> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserVO> findByUserId(Long userId);

    void updateUserInfo(
            @Param("userId") Long userId,
            @Param("nickname") String nickname,
            @Param("gender") Gender gender,
            @Param("birthDate") LocalDate birthDate);

    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}
