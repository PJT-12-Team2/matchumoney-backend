package team2.pjt12.matchumoney.domain.depositsearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import team2.pjt12.matchumoney.domain.depositsearch.service.DepositSearchService;

import java.util.List;

@RestController
@RequestMapping("/api/deposit")
@RequiredArgsConstructor
public class DepositSearchController {

    private final DepositSearchService depositSearchService;

    @PostMapping("/search")
    public List<DepositSearchResponseDTO> searchDepositProducts(@RequestBody DepositSearchRequestDTO request) {
        return depositSearchService.searchDepositProducts(request);
    }
}