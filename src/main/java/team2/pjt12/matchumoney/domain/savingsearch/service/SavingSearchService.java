package team2.pjt12.matchumoney.domain.savingsearch.service;

import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;

import java.util.List;

public interface SavingSearchService {
    List<SavingSearchResponseDTO> searchSavingProducts(SavingSearchRequestDTO request);
}
