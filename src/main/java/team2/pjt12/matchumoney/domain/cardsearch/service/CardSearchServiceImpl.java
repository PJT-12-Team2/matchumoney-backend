package team2.pjt12.matchumoney.domain.cardsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.cardsearch.mapper.CardSearchMapper;
import team2.pjt12.matchumoney.domain.personacard.mapper.PersonaCardMapper;

import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class CardSearchServiceImpl implements CardSearchService {
    private final CardSearchMapper cardSearchMapper;
    private final PersonaCardMapper personaCardMapper;

    @Override
    public List<CardSearchResponseDTO> searchCards(CardSearchRequestDTO request) {
        Long userId = getCurrentUser().getUserId();
        List<CardSearchResponseDTO> cards = cardSearchMapper.selectCardsByFilter(request, userId);
        for (CardSearchResponseDTO card : cards) {
            List<CardOptionDTO> options = personaCardMapper.selectCardOptionsByCardId(card.getId());
            card.setOptions(options);
        }
        return cards;
    }

//    private List<PersonaCardDTO> getRandomizedCards(Long personaId) {
//        List<PersonaCardDTO> allCards = personaCardMapper.selectCardsByPersonaId(personaId);
//        log.info("🎯 조회된 카드 수: {}", allCards.size());
//        Collections.shuffle(allCards);
//
//        List<PersonaCardDTO> recommendedCards = allCards.stream()
//                .limit(RECOMMENDATION_LIMIT)
//                .toList();
//
//        log.info("🎯 실제 추천 카드 수: {}", recommendedCards.size());
//
//        for (PersonaCardDTO card : recommendedCards) {
//            List<CardOptionDTO> options = personaCardMapper.selectCardOptionsByCardId(card.getCardId());
//            card.setOptions(options);
//        }
//
//        return recommendedCards;
//    }
}
