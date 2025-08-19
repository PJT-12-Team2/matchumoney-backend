package team2.pjt12.matchumoney.domain.savingsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;
import team2.pjt12.matchumoney.domain.savingsearch.service.SavingSearchService;

import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@RestController
@RequestMapping("/api/saving")
@RequiredArgsConstructor
@Api(tags = "Saving Search API", description = "적금 상품 검색 API")
public class SavingSearchController {

    private final SavingSearchService savingSearchService;

    @PostMapping("/search")
    @ApiOperation(value = "적금 상품 검색(필터링)", notes = "은행명 및 저축 금액 조건으로 적금 상품 검색")
    public List<SavingSearchResponseDTO> search(
            @ApiParam(value = "적금 상품 검색 조건 DTO") @RequestBody SavingSearchRequestDTO req) {
        Long userId = getCurrentUser().getUserId();
        return savingSearchService.searchSavingProducts(userId, req);
    }
}