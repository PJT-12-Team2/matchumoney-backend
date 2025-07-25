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

    boolean isExistsByEmail(String email);

    Optional<UserVO> findByUserId(Long userId);

    void updateUserInfo(
            @Param("userId") Long userId,
            @Param("nickname") String nickname,
            @Param("gender") Gender gender,
            @Param("birthDate") LocalDate birthDate);

    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    // 예금 즐겨찾기 추가
    void addDepositFavorite(@Param("userId") Long userId,
                            @Param("productId") Long productId);

    // 적금 즐겨찾기 추가
    void addSavingFavorite(@Param("userId") Long userId,
                           @Param("productId") Long productId);

    // 카드 즐겨찾기 추가
    void addCardFavorite(@Param("userId") Long userId,
                         @Param("productId") Long productId);

    boolean isDepositFavoriteExists(@Param("userId") Long userId,
                                    @Param("productId") Long productId);

    boolean isSavingFavoriteExists(@Param("userId") Long userId,
                                   @Param("productId") Long productId);

    boolean isCardFavoriteExists(@Param("userId") Long userId,
                                 @Param("productId") Long productId);
}
