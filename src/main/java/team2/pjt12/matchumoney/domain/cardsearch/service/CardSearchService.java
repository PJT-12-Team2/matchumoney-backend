package team2.pjt12.matchumoney.domain.cardsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.cardsearch.mapper.CardSearchMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardSearchService {

    private final CardSearchMapper cardSearchMapper;

    public List<CardSearchResponseDTO> searchCards(CardSearchRequestDTO request) {
        return cardSearchMapper.selectCardsByFilter(request);
    }
}
