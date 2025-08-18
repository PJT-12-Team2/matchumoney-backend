package team2.pjt12.matchumoney.domain.cardsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardListItemDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.service.CardSearchService;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/persona")
@RequiredArgsConstructor
@Validated
@Api(tags = "Card Search API", description = "카드 검색 API")
public class CardSearchController {
    private final CardSearchService cardSearchService;

    @PostMapping("/cardsearch")
    @ApiOperation(
            value = "카드 검색(페이지네이션)",
            notes = "신용/체크/혜택 필터로 검색하고 page/size 기준으로 페이지네이션합니다."
    )
    public ResponseEntity<List<CardListItemDTO>> searchCardsPage(
            @ApiParam(value = "검색 필터", required = true)
            @RequestBody @Valid CardSearchRequestDTO req,
            @ApiParam(value = "페이지 번호(0부터)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @ApiParam(value = "페이지 크기", example = "12")
            @RequestParam(defaultValue = "12") @Min(1) int size
    ) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<CardListItemDTO> result = cardSearchService.searchCards(userId, req, page, size);
        return ResponseEntity.ok(result);
    }
}
