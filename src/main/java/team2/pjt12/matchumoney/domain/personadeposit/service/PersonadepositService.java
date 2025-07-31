package team2.pjt12.matchumoney.domain.personadeposit.service;

import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonadepositResponseDTO;


public interface PersonadepositService {
    PersonadepositResponseDTO getRecommendedDeposit(Long personaId);
}
