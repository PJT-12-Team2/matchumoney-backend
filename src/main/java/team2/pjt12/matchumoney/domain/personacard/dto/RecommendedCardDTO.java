package team2.pjt12.matchumoney.domain.personacard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecommendedCardDTO {
    private Long cardProductId;
    private String name;
    private String cardImageUrl;
}
