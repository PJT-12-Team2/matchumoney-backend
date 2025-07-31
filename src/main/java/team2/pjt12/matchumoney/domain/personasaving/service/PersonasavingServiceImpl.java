package team2.pjt12.matchumoney.domain.personasaving.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonasavingDTO;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonasavingResponseDTO;
import team2.pjt12.matchumoney.domain.personasaving.mapper.PersonasavingMapper;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonasavingServiceImpl implements PersonasavingService {
    private final PersonasavingMapper personasavingMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    // personaId로 personaName 조회
    @Override
    public PersonasavingResponseDTO getRecommendedSaving(Long personaId) {
        String personaName = personasavingMapper.selectPersonaNameById(personaId);
        List<PersonasavingDTO> recommendedSavings = getRandomizedSavings(personaId);

        return buildRecommendationResponse(personaName, recommendedSavings);
    }

    // personaId에 맞는 적금 추천 3개
    private List<PersonasavingDTO> getRandomizedSavings(Long personaId) {
        List<PersonasavingDTO> allSavings = personasavingMapper.selectSavingsByPersonaId(personaId);
        Collections.shuffle(allSavings);
        return allSavings.stream()
                .limit(RECOMMENDATION_LIMIT).toList();
    }

    // build
    private PersonasavingResponseDTO buildRecommendationResponse(
            String personaName, List<PersonasavingDTO> savings) {
        return PersonasavingResponseDTO.builder()
                .personaName(personaName)
                .savings(savings)
                .build();
    }
}
