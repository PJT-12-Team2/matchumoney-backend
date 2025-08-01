package team2.pjt12.matchumoney.domain.user.dto.res;

import lombok.Getter;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.persona.dto.PersonaSimpleResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;

import java.util.List;

@Getter
public final class MyPageResponseDTO {
    private final String nickname;
    private final PersonaSimpleResponseDTO persona;
    private final Integer exp;

    private final List<DepositProductResponseDTO> favoriteDeposits;
    private final List<SavingListItemResponseDTO> favoriteSavings;
    private final List<CardSearchResponseDTO> favoriteCards;

    public MyPageResponseDTO(
            String nickname,
            PersonaSimpleResponseDTO persona,
            Integer exp,
            List<DepositProductResponseDTO> favoriteDeposits,
            List<SavingListItemResponseDTO> favoriteSavings,
            List<CardSearchResponseDTO> favoriteCards
    ) {
        this.nickname = nickname;
        this.persona = persona;
        this.exp = exp;
        this.favoriteDeposits = favoriteDeposits;
        this.favoriteSavings = favoriteSavings;
        this.favoriteCards = favoriteCards;
    }
}
