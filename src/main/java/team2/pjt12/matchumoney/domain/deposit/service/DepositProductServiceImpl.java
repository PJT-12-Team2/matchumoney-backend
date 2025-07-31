package team2.pjt12.matchumoney.domain.deposit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.mapper.DepositProductMapper;
import team2.pjt12.matchumoney.domain.deposit.util.AmountExtractorUtil; // 🆕 추가

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositProductServiceImpl implements DepositProductService {

    private final DepositProductMapper depositProductMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DepositProductResponseDTO> getAllDepositProducts() {
        List<DepositProductResponseDTO> products = depositProductMapper.findAllDepositProducts();

        // 각 상품의 최소 금액 추출 및 설정 (유틸리티 사용)
        products.forEach(product -> {
            String minAmount = AmountExtractorUtil.extractMinAmount(product.getEtcNote());
            product.setMinAmount(minAmount);
        });

        return products;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepositProductResponseDTO> getDepositProductsByBank(String bankName) {
        log.info("은행별 예금 상품 조회 요청 - 은행명: {}", bankName);

        if (bankName == null || bankName.trim().isEmpty()) {
            log.warn("은행명이 비어있음");
            throw new IllegalArgumentException("은행명은 필수입니다");
        }

        List<DepositProductResponseDTO> products = depositProductMapper.findDepositProductsByBankName(bankName.trim());

        log.info("{}의 예금 상품 수: {}", bankName, products.size());
        return products;
    }
}