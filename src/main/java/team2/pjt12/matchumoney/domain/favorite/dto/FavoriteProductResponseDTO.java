package team2.pjt12.matchumoney.domain.favorite.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardDTO;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositDTO;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingDTO;

import java.util.List;

@Getter
@ApiModel(description = "즐겨찾기 상품 목록 응답 DTO")
public final class FavoriteProductResponseDTO {
    @ApiModelProperty(value = "즐겨찾기 카드 목록")
    private final List<PersonaCardDTO> cardList;

    @ApiModelProperty(value = "즐겨찾기 적금 목록")
    private final List<PersonaSavingDTO> savingList;

    @ApiModelProperty(value = "즐겨찾기 예금 목록")
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