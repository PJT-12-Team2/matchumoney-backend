package team2.pjt12.matchumoney.domain.personacard.service;

import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardRecommendationResponseDTO;

public interface PersonacardService {
    PersonaCardRecommendationResponseDTO getRecommendedCards(Long personaId);
}
