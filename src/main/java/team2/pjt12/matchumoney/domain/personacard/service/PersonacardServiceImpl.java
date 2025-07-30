package team2.pjt12.matchumoney.domain.personacard.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonacardResponseDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonacardDTO;
import team2.pjt12.matchumoney.domain.personacard.mapper.PersonacardMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonacardServiceImpl implements PersonacardService {

    private final PersonacardMapper personacardMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    // personaId로 personaName 조회
    @Override
    public PersonacardResponseDTO getRecommendedCards(Long personaId) {
        String personaName = personacardMapper.selectPersonaNameById(personaId);
        List<PersonacardDTO> recommendedCards = getRandomizedCards(personaId);

        return buildRecommendationResponse(personaName, recommendedCards);
    }

    // personaId에 맞는 카드 추천 3개
    private List<PersonacardDTO> getRandomizedCards(Long personaId) {
        List<PersonacardDTO> allCards = personacardMapper.selectCardsByPersonaId(personaId);
        Collections.shuffle(allCards);
        return allCards.stream()
                .limit(RECOMMENDATION_LIMIT).toList();
    }

    // build
    private PersonacardResponseDTO buildRecommendationResponse(String personaName, List<PersonacardDTO> cards) {
        return PersonacardResponseDTO.builder()
                .personaName(personaName)
                .cards(cards)
                .build();
    }
}
