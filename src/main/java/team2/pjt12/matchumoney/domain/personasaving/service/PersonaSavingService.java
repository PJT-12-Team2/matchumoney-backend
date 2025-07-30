package team2.pjt12.matchumoney.domain.personasaving.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personasaving.dto.SavingProductDTO;
import team2.pjt12.matchumoney.domain.personasaving.mapper.PersonaSavingMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaSavingService {

    private final PersonaSavingMapper mapper;

    public List<SavingProductDTO> getRecommendedByPersona(Long personaId) {
        return mapper.findByPersonaId(personaId);
    }
}
