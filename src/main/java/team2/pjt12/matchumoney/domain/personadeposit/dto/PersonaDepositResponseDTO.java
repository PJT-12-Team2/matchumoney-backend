package team2.pjt12.matchumoney.domain.personadeposit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDepositResponseDTO {
    private String personaName;
    private List<PersonaDepositDTO> deposits;
}
