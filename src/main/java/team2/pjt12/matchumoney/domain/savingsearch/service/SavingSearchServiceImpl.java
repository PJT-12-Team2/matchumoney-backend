package team2.pjt12.matchumoney.domain.savingsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingOptionDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;
import team2.pjt12.matchumoney.domain.savingsearch.mapper.SavingSearchMapper;

import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class SavingSearchServiceImpl implements SavingSearchService {

    private final SavingSearchMapper savingSearchMapper;

    @Override
    public List<SavingSearchResponseDTO> searchSavingProducts(SavingSearchRequestDTO request) {
        Long userId = getCurrentUser().getUserId();
        List<SavingSearchResponseDTO> products = savingSearchMapper.findAllSavingProducts(request, userId);

        for (SavingSearchResponseDTO product : products) {
            List<SavingOptionDTO> options = savingSearchMapper.findOptionsByProductId(product.getFinPrdtCd());
            product.setSavingOptions(options);
        }

        return products;
    }
}
