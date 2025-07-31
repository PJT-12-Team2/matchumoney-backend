package team2.pjt12.matchumoney.domain.personacard.service;

import team2.pjt12.matchumoney.domain.personacard.dto.PersonacardResponseDTO;

public interface PersonacardService {
    PersonacardResponseDTO getRecommendedCards(Long personaId);
}
