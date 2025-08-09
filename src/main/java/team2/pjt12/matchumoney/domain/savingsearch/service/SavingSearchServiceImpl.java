package team2.pjt12.matchumoney.domain.savingsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositOptionDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
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
    public List<SavingSearchResponseDTO> searchSavingProducts(Long userId, SavingSearchRequestDTO req) {
        List<SavingSearchResponseDTO> list =
                savingSearchMapper.findAllSavingProducts(userId, req.getKorCoNm(), req.getMaxLimit());

        for (SavingSearchResponseDTO p : list) {
            p.setSavingOptions(
                    savingSearchMapper.findOptionsByProductId(p.getFinPrdtCd())
            );
        }
        return list;
    }
}

