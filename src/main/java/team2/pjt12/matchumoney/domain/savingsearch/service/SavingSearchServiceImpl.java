package team2.pjt12.matchumoney.domain.savingsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingOptionDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;
import team2.pjt12.matchumoney.domain.savingsearch.mapper.SavingSearchMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingSearchServiceImpl implements SavingSearchService {

    private final SavingSearchMapper savingSearchMapper;

    @Override
    public List<SavingSearchResponseDTO> searchSavingProducts(SavingSearchRequestDTO request) {
        List<SavingSearchResponseDTO> products = savingSearchMapper.findAllSavingProducts(request);

        for (SavingSearchResponseDTO product : products) {
            List<SavingOptionDTO> options = savingSearchMapper.findOptionsByProductId(product.getFinPrdtCd());
            product.setSavingOptions(options);
        }

        return products;
    }
}
