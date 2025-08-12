package team2.pjt12.matchumoney.domain.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.favorite.domain.FavoriteVO;
import team2.pjt12.matchumoney.domain.persona.dto.PersonaResponseDTO;
import team2.pjt12.matchumoney.domain.persona.dto.PersonaSimpleResponseDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardDTO;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositDTO;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;
import team2.pjt12.matchumoney.domain.user.domain.Gender;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.dto.res.MyPageResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    void deleteFavorite(@Param("userId") Long userId,
                        @Param("productId") Long productId,
                        @Param("productType") String productType);

    List<FavoriteVO> getFavorites(@Param("userId") Long userId);

    List<PersonaCardDTO> getFavoriteCards(Long userId);
    List<PersonaSavingDTO> getFavoriteSavings(Long userId);
    List<PersonaDepositDTO> getFavoriteDeposits(Long userId);

    List<DepositProductResponseDTO> getSimpleFavoriteDeposits(@Param("userId") Long userId);
    List<SavingListItemResponseDTO> getSimpleFavoriteSavings(@Param("userId") Long userId);
    List<CardSearchResponseDTO> getSimpleFavoriteCards(@Param("userId") Long userId);

    void updatePersona(@Param("userId") Long userId, @Param("personaId") String personaId);

    Optional<PersonaSimpleResponseDTO> getPersonaByUserId(@Param("userId") Long userId);
    
    // 경험치(EXP) 업데이트
    void updateUserExp(@Param("userId") Long userId, @Param("expAmount") int expAmount);

    // 상위 퍼센트용
    Long percentileFindExpByUserId(@Param("userId") Long userId);
    Long percentileCountAllUsers();
    Map<String, Object> percentileCountLowerAndEqualByExp(@Param("exp") long exp);
}
