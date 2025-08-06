package team2.pjt12.matchumoney.domain.depositdetail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.mapper.DepositDetailMapper;

@Service
@RequiredArgsConstructor
public class DepositDetailServiceImpl implements DepositDetailService {

    private final DepositDetailMapper depositDetailMapper;

    @Override
    public DepositDetailResponseDTO getDepositDetailById(Long id) {
        DepositDetailResponseDTO product = depositDetailMapper.findDepositProductById(id);
        product.setOptions(depositDetailMapper.findOptionsByProductId(id));
        return product;
    }
}