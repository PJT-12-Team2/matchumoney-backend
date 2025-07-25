package team2.pjt12.matchumoney.domain.personacard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PersonaCardRecommendationResponseDTO {
    private PersonaInfoDTO persona;
    private List<RecommendedCardDTO> cards;
}
