package team2.pjt12.matchumoney.domain.cardsearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardSearchRequestDTO {
    private boolean creditCard;
    private boolean debitCard;
    private List<String> selectedBenefits;
}
