package team2.pjt12.matchumoney.domain.depositsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositOptionDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import team2.pjt12.matchumoney.domain.depositsearch.mapper.DepositSearchMapper;


import java.util.List;

@Service
@RequiredArgsConstructor
public class DepositSearchServiceImpl implements DepositSearchService {

    private final DepositSearchMapper depositSearchMapper;

    @Override
    public List<DepositSearchResponseDTO> searchDepositProducts(DepositSearchRequestDTO request) {
        List<DepositSearchResponseDTO> products = depositSearchMapper.findAllDepositProducts(request);

        for (DepositSearchResponseDTO product : products) {
            List<DepositOptionDTO> options = depositSearchMapper.findOptionsByProductId(product.getFinPrdtCd());
            product.setDepositOptions(options);
        }

        return products;
    }
}
