package team2.pjt12.matchumoney.domain.personacard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PersonaInfoDTO {
    private Long personaId;
    private String personaName;
}

