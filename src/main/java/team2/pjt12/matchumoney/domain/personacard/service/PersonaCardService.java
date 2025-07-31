package team2.pjt12.matchumoney.domain.personacard.service;

import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardResponseDTO;

public interface PersonaCardService {
    PersonaCardResponseDTO getRecommendedCards(Long personaId);
}
