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
            value = "카드 검색(전체 반환)",
            notes = "신용/체크 여부 및 혜택 선택 조건에 따라 카드를 검색해 한 번에 모두 반환합니다."
    )
    public ResponseEntity<List<CardSearchResponseDTO>> searchCards(
            @ApiParam(value = "검색 조건(신용/체크/혜택 목록)", required = true)
            @RequestBody CardSearchRequestDTO request) {
        if (request.getSelectedBenefits() == null) {
            request = new CardSearchRequestDTO(
                    request.isCreditCard(),
                    request.isDebitCard(),
                    Collections.emptyList()
            );
        }

        List<CardSearchResponseDTO> result = cardSearchService.searchCards(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/infinite")
    @ApiOperation(
            value = "카드 검색(커서 기반 무한스크롤)",
            notes = "커서 기반 페이지네이션으로 카드를 검색합니다."
    )
    public CursorPageResponse<CardListItemDTO> searchInfinite(
            @RequestParam(required = false) String cursor,     // 마지막 커서(다음 페이지 시작점)
            @RequestParam(defaultValue = "6") int size,        // 페이지 기본 6개
            @ApiParam(value = "검색 조건(신용/체크/혜택 목록)", required = true)
            @RequestBody CardSearchRequestDTO req
    ) {
        Long userId = SecurityUtils.getCurrentUser().getUserId();
        return cardSearchService.searchInfinite(userId, req, cursor, size);
    }
}
