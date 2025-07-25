package team2.pjt12.matchumoney.domain.savingsearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;
import team2.pjt12.matchumoney.domain.savingsearch.service.SavingSearchService;

import java.util.List;

@RestController
@RequestMapping("/api/saving")
@RequiredArgsConstructor
public class SavingSearchController {

    private final SavingSearchService savingSearchService;

    @PostMapping("/search")
    public List<SavingSearchResponseDTO> searchSavingProducts(@RequestBody SavingSearchRequestDTO request) {
        return savingSearchService.searchSavingProducts(request);
    }
}