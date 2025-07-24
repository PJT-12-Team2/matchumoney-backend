package team2.pjt12.matchumoney.domain.cardsearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.cardsearch.service.CardSearchService;

import java.util.List;

@RestController
@RequestMapping("/api/persona")
@RequiredArgsConstructor
public class CardSearchController {

    private final CardSearchService cardSearchService;

    @PostMapping("/cardsearch")
    public ResponseEntity<List<CardSearchResponseDTO>> searchCards(@RequestBody CardSearchRequestDTO request) {
        List<CardSearchResponseDTO> result = cardSearchService.searchCards(request);
        return ResponseEntity.ok(result);
    }
}
