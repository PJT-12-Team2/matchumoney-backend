package team2.pjt12.matchumoney.domain.persona.service;

import team2.pjt12.matchumoney.domain.persona.dto.PersonaResponseDTO;

public interface PersonaService {
    PersonaResponseDTO getPersonaDetail(String code);
    PersonaResponseDTO getPersonaDetailById(Long personaId);

}
