package team2.pjt12.matchumoney.domain.user.dto.res;

import lombok.Getter;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.persona.dto.PersonaSimpleResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public final class MyPageResponseDTO {
    private final String nickname;
    private final PersonaSimpleResponseDTO persona;
    private final Integer exp;

    private final String email;
    private final String profileImageUrl;
    private final Boolean socialLogin;      // maps is_social_login
    private final Long favoriteId;          // optional: remove if not used in schema
    private final String gender;            // "MALE" | "FEMALE"
    private final LocalDate birthDate;
    private final LocalDateTime createdTime;
    private final LocalDateTime lastModifiedTime;

    private final List<DepositProductResponseDTO> favoriteDeposits;
    private final List<SavingListItemResponseDTO> favoriteSavings;
    private final List<CardSearchResponseDTO> favoriteCards;

    public MyPageResponseDTO(
            String nickname,
            PersonaSimpleResponseDTO persona,
            Integer exp,
            String email,
            String profileImageUrl,
            Boolean socialLogin,
            Long favoriteId,
            String gender,
            LocalDate birthDate,
            LocalDateTime createdTime,
            LocalDateTime lastModifiedTime,
            List<DepositProductResponseDTO> favoriteDeposits,
            List<SavingListItemResponseDTO> favoriteSavings,
            List<CardSearchResponseDTO> favoriteCards
    ) {
        this.nickname = nickname;
        this.persona = persona;
        this.exp = exp;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.socialLogin = socialLogin;
        this.favoriteId = favoriteId;
        this.gender = gender;
        this.birthDate = birthDate;
        this.createdTime = createdTime;
        this.lastModifiedTime = lastModifiedTime;
        this.favoriteDeposits = favoriteDeposits;
        this.favoriteSavings = favoriteSavings;
        this.favoriteCards = favoriteCards;
    }
}
