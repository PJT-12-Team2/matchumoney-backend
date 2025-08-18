package team2.pjt12.matchumoney.domain.cardsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardListItemDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CursorPageResponse;
import team2.pjt12.matchumoney.domain.cardsearch.service.CardSearchService;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/persona")
@RequiredArgsConstructor
@Api(tags = "Card Search", description = "카드 검색 API")
public class CardSearchController {
    private final CardSearchService cardSearchService;

    @PostMapping("/cardsearch")
    @ApiOperation(
            value = "카드 검색(페이지네이션)",
            notes = "신용/체크/혜택 필터로 검색하고 page/size 기준으로 페이지네이션합니다."
    )
    public ResponseEntity<List<CardListItemDTO>> searchCardsPage(
            @RequestBody CardSearchRequestDTO req,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<CardListItemDTO> result = cardSearchService.searchCards(userId, req, page, size);
        return ResponseEntity.ok(result);
    }
}
