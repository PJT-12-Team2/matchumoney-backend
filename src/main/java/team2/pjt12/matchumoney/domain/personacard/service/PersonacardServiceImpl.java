package team2.pjt12.matchumoney.domain.personacard.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardRecommendationResponseDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaInfoDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.RecommendedCardDTO;
import team2.pjt12.matchumoney.domain.personacard.mapper.PersonacardMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonacardServiceImpl implements PersonacardService {

    private final PersonacardMapper personacardMapper;

    @Override
    public PersonaCardRecommendationResponseDTO getRecommendedCards(Long personaId) {
        String personaName = personacardMapper.findPersonaNameById(personaId);
        List<RecommendedCardDTO> allCards = personacardMapper.findCardsByPersonaId(personaId);

        Collections.shuffle(allCards);
        List<RecommendedCardDTO> recommendedCards = allCards.stream()
                .limit(3)
                .collect(Collectors.toList());

        return PersonaCardRecommendationResponseDTO.builder()
                .persona(PersonaInfoDTO.builder()
                        .personaId(personaId)
                        .personaName(personaName)
                        .build())
                .cards(recommendedCards)
                .build();
    }
}
