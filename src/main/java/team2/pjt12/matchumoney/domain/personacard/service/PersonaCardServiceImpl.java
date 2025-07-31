package team2.pjt12.matchumoney.domain.personacard.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardResponseDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardDTO;
import team2.pjt12.matchumoney.domain.personacard.mapper.PersonaCardMapper;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaCardServiceImpl implements PersonaCardService {

    private final PersonaCardMapper personaCardMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    // personaId로 personaName 조회
    @Override
    public PersonaCardResponseDTO getRecommendedCards(Long personaId) {
        String personaName = personaCardMapper.selectPersonaNameById(personaId);
        List<PersonaCardDTO> recommendedCards = getRandomizedCards(personaId);

        return buildRecommendationResponse(personaName, recommendedCards);
    }

    // personaId에 맞는 카드 추천 3개
    private List<PersonaCardDTO> getRandomizedCards(Long personaId) {
        List<PersonaCardDTO> allCards = personaCardMapper.selectCardsByPersonaId(personaId);
        Collections.shuffle(allCards);
        return allCards.stream()
                .limit(RECOMMENDATION_LIMIT).toList();
    }

    // build
    private PersonaCardResponseDTO buildRecommendationResponse(String personaName, List<PersonaCardDTO> cards) {
        return PersonaCardResponseDTO.builder()
                .personaName(personaName)
                .cards(cards)
                .build();
    }
}
