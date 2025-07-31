package team2.pjt12.matchumoney.domain.personadeposit.service;

import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;


public interface PersonaDepositService {
    PersonaDepositResponseDTO getRecommendedDeposit(Long personaId);

    Long getPersonaIdByUserId(Long userId);
}
