package team2.pjt12.matchumoney.domain.cardsearch.service;

import team2.pjt12.matchumoney.domain.cardsearch.dto.CardListItemDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CursorPageResponse;

import java.util.List;

public interface CardSearchService {
    List<CardListItemDTO> searchCards(Long userId, CardSearchRequestDTO req, int page, int size);
    CursorPageResponse<CardListItemDTO> searchInfinite(Long userId, CardSearchRequestDTO req, String cursor, int size);
}
