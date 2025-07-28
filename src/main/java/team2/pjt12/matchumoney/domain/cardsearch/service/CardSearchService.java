package team2.pjt12.matchumoney.domain.cardsearch.service;

import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;

import java.util.List;

public interface CardSearchService {
    List<CardSearchResponseDTO> searchCards(CardSearchRequestDTO request);
}
