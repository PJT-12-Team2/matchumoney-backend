package team2.pjt12.matchumoney.domain.deposit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.mapper.DepositProductMapper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositProductServiceImpl implements DepositProductService {

    private final DepositProductMapper depositProductMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DepositProductResponseDTO> getAllDepositProducts() {
        List<DepositProductResponseDTO> products = depositProductMapper.findAllDepositProducts();

        // 각 상품의 최소 금액 추출 및 설정
        products.forEach(product -> {
            String minAmount = extractMinAmount(product.getEtcNote());
            product.setMinAmount(minAmount);

        });

        return products;
    }

    private String extractMinAmount(String etcNote) {
        if (etcNote == null || etcNote.trim().isEmpty()) {
            return "정보 없음";
        }

        // 정규식으로 금액 추출
        Pattern pattern = Pattern.compile("([0-9]+(?:[천백십]*[만]?)+원)");
        Matcher matcher = pattern.matcher(etcNote);

        if (matcher.find()) {
            String amount = matcher.group(1);
            String converted = convertKoreanNumber(amount);
            return converted;
        } else {
            return "정보 없음";
        }
    }

    private String convertKoreanNumber(String amount) {
        // 천백십 -> 000, 00, 0 변환 로직
        return amount.replace("천", "000")
                .replace("백", "00")
                .replace("십", "0");
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

    @Override
    @Transactional(readOnly = true)
    public DepositProductResponseDTO getDepositProductByCode(String productCode) {
        log.info("상품 코드별 예금 상품 조회 요청 - 상품코드: {}", productCode);

        if (productCode == null || productCode.trim().isEmpty()) {
            log.warn("상품코드가 비어있음");
            throw new IllegalArgumentException("상품코드는 필수입니다");
        }

        DepositProductResponseDTO product = depositProductMapper.findDepositProductByCode(productCode.trim());

        if (product == null) {
            log.warn("상품을 찾을 수 없음 - 상품코드: {}", productCode);
            throw new IllegalArgumentException("해당 상품코드의 예금 상품을 찾을 수 없습니다: " + productCode);
        }

        log.info("상품 조회 성공 - 상품명: {}", product.getProductName());
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepositProductResponseDTO> getDepositProductsByInterestRate(Double minRate, Double maxRate) {
        log.info("금리 범위별 예금 상품 조회 요청 - 최소금리: {}, 최대금리: {}", minRate, maxRate);

        // 금리 범위 유효성 검사
        if (minRate != null && minRate < 0) {
            log.warn("최소금리가 음수임: {}", minRate);
            throw new IllegalArgumentException("최소금리는 0 이상이어야 합니다");
        }

        if (maxRate != null && maxRate < 0) {
            log.warn("최대금리가 음수임: {}", maxRate);
            throw new IllegalArgumentException("최대금리는 0 이상이어야 합니다");
        }

        if (minRate != null && maxRate != null && minRate > maxRate) {
            log.warn("최소금리가 최대금리보다 큼 - 최소: {}, 최대: {}", minRate, maxRate);
            throw new IllegalArgumentException("최소금리는 최대금리보다 작거나 같아야 합니다");
        }

        List<DepositProductResponseDTO> products = depositProductMapper.findDepositProductsByInterestRate(minRate, maxRate);

        log.info("금리 범위({} ~ {})에 해당하는 예금 상품 수: {}", minRate, maxRate, products.size());
        return products;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepositProductResponseDTO> getDepositProductsBySaveTerm(Integer saveTrm) {
        log.info("저축 기간별 예금 상품 조회 요청 - 저축기간: {}개월", saveTrm);

        if (saveTrm == null || saveTrm <= 0) {
            log.warn("저축기간이 유효하지 않음: {}", saveTrm);
            throw new IllegalArgumentException("저축기간은 1개월 이상이어야 합니다");
        }

        List<DepositProductResponseDTO> products = depositProductMapper.findDepositProductsBySaveTerm(saveTrm);

        log.info("{}개월 저축기간을 지원하는 예금 상품 수: {}", saveTrm, products.size());
        return products;
    }
}