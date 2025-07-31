package team2.pjt12.matchumoney.domain.personasaving.service;

import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingResponseDTO;

public interface PersonaSavingService {
    PersonaSavingResponseDTO getRecommendedSaving(Long personaId);
}