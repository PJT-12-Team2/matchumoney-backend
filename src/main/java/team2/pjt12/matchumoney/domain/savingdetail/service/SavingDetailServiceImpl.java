package team2.pjt12.matchumoney.domain.savingdetail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.mapper.SavingDetailMapper;

@Service
@RequiredArgsConstructor
public class SavingDetailServiceImpl implements SavingDetailService {

    private final SavingDetailMapper savingDetailMapper;

    @Override
    public SavingDetailResponseDTO getSavingDetailById(Long id) {
        SavingDetailResponseDTO product = savingDetailMapper.findSavingProductById(id);
        product.setOptions(savingDetailMapper.findOptionsByProductId(product.getFinPrdtCd()));
        return product;
    }
}