package team2.pjt12.matchumoney.domain.personacard.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardResponseDTO;
import team2.pjt12.matchumoney.domain.personacard.mapper.PersonaCardMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonaCardServiceImpl implements PersonaCardService {

    private final PersonaCardMapper personaCardMapper;
    private final UserMapper userMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    // personaId로 personaName 조회
    @Override
    public PersonaCardResponseDTO getRecommendedCards(Long personaId) {
        log.info("⭐ personaId: {}", personaId);
        String personaName = personaCardMapper.selectPersonaNameById(personaId);
        List<PersonaCardDTO> recommendedCards = getRandomizedCards(personaId);

        return buildRecommendationResponse(personaName, recommendedCards);
    }

    // personaId에 맞는 카드 추천 3개
    private List<PersonaCardDTO> getRandomizedCards(Long personaId) {
        List<PersonaCardDTO> allCards = personaCardMapper.selectCardsByPersonaId(personaId);
        log.info("🎯 조회된 카드 수: {}", allCards.size());
        Collections.shuffle(allCards);

        List<PersonaCardDTO> recommendedCards = allCards.stream()
                .limit(RECOMMENDATION_LIMIT)
                .toList();

        log.info("🎯 실제 추천 카드 수: {}", recommendedCards.size());

        for (PersonaCardDTO card : recommendedCards) {
            List<CardOptionDTO> options = personaCardMapper.selectCardOptionsByCardId(card.getCardId());
            card.setOptions(options);
        }

        return recommendedCards;
    }

    // build
    private PersonaCardResponseDTO buildRecommendationResponse(String personaName, List<PersonaCardDTO> cards) {
        return PersonaCardResponseDTO.builder()
                .personaName(personaName)
                .cards(cards)
                .build();
    }
    @Override
    public Long getPersonaIdByUserId(Long userId) {
        return userMapper.findByUserId(userId)
                .map(UserVO::getPersonaId)
                .orElseThrow(() -> new RuntimeException("해당 유저의 persona_id를 찾을 수 없습니다."));
    }
}
