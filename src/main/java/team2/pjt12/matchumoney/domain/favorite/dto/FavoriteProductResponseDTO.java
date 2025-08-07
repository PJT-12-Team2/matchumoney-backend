package team2.pjt12.matchumoney.domain.favorite.dto;

import lombok.Getter;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardDTO;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositDTO;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingDTO;

import java.util.List;

@Getter
public final class FavoriteProductResponseDTO {
    private final List<PersonaCardDTO> cardList;
    private final List<PersonaSavingDTO> savingList;
    private final List<PersonaDepositDTO> depositList;

    public FavoriteProductResponseDTO(
            List<PersonaCardDTO> cardList,
            List<PersonaSavingDTO> savingList,
            List<PersonaDepositDTO> depositList
    ) {
        this.cardList = cardList;
        this.savingList = savingList;
        this.depositList = depositList;
    }
}