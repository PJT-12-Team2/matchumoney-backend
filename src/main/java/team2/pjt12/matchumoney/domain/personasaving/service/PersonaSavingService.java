package team2.pjt12.matchumoney.domain.personasaving.service;

import team2.pjt12.matchumoney.domain.personasaving.dto.PersonasavingResponseDTO;

public interface PersonasavingService {
    PersonasavingResponseDTO getRecommendedSaving(Long personaId);
}