package team2.pjt12.matchumoney.domain.depositsearch.service;

import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;


import java.util.List;

public interface DepositSearchService {
    List<DepositSearchResponseDTO> searchDepositProducts(DepositSearchRequestDTO request);
}
