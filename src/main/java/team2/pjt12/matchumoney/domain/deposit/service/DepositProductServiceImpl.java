package team2.pjt12.matchumoney.domain.deposit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.mapper.DepositProductMapper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositProductServiceImpl implements DepositProductService {

    private final DepositProductMapper depositProductMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DepositProductResponseDTO> getAllDepositProducts() {
        List<DepositProductResponseDTO> products = depositProductMapper.findAllDepositProducts();

        // 각 상품의 최소 금액 추출 및 설정 (직접 처리)
        products.forEach(product -> {
            String minAmount = extractMinAmountAsString(product.getEtcNote());
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

        // 은행별 조회에서도 minAmount 설정
        products.forEach(product -> {
            String minAmount = extractMinAmountAsString(product.getEtcNote());
            product.setMinAmount(minAmount);
        });

        log.info("{}의 예금 상품 수: {}", bankName, products.size());
        return products;
    }

    @Override
    public List<DepositProductResponseDTO> getProductsByBalance(BalanceRequestDTO request) {
        log.info("잔액 기반 상품 추천 시작: userId={}, balance={}", request.getUserId(), request.getBalance());

        try {
            // 1. 입력 검증
            if (request.getUserId() == null || request.getBalance() == null) {
                log.error("필수 파라미터 누락: userId={}, balance={}", request.getUserId(), request.getBalance());
                throw new IllegalArgumentException("userId와 balance는 필수입니다");
            }

            // 2. 모든 상품 조회
            List<DepositProductResponseDTO> allProducts = depositProductMapper.findAllDepositProducts();
            log.info("전체 상품 수: {}", allProducts.size());

            // 3. 잔액 기반 필터링
            List<DepositProductResponseDTO> filteredProducts = allProducts.stream()
                    .filter(product -> {
                        try {
                            // 최소 금액 추출
                            Long minAmount = extractMinAmountAsLong(product.getEtcNote());

                            // 금액 추출 실패한 상품은 포함 (제한 없음으로 간주)
                            if (minAmount == -1L) {
                                return true;
                            }

                            // 사용자 잔액이 최소 금액 이상인 경우만 포함
                            return request.getBalance() >= minAmount;
                        } catch (Exception e) {
                            log.warn("상품 필터링 중 오류: {}", e.getMessage());
                            return true; // 오류 시 포함
                        }
                    })
                    .collect(Collectors.toList());

            // 4. 화면 표시용 minAmount 설정
            filteredProducts.forEach(product -> {
                String minAmount = extractMinAmountAsString(product.getEtcNote());
                product.setMinAmount(minAmount);
            });

            log.info("필터링된 상품 수: {} (전체: {})", filteredProducts.size(), allProducts.size());
            return filteredProducts;

        } catch (Exception e) {
            log.error("getProductsByBalance 실행 중 오류", e);
            throw new RuntimeException("상품 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 문자열로 최소 금액 추출 (화면 표시용)
     */
    private String extractMinAmountAsString(String etcNote) {
        if (etcNote == null || etcNote.trim().isEmpty()) {
            return "제한 없음";
        }

        try {
            Pattern pattern = Pattern.compile("([0-9,]+(?:만|천|백)?)[\\s]*원");
            Matcher matcher = pattern.matcher(etcNote);

            if (matcher.find()) {
                return matcher.group(1) + "원";
            }
        } catch (Exception e) {
            log.warn("최소 금액 문자열 추출 실패: {}", e.getMessage());
        }

        return "제한 없음";
    }

    /**
     * etcNote에서 최소 금액을 추출하여 Long 타입으로 반환 (계산용)
     */
    private Long extractMinAmountAsLong(String etcNote) {
        if (etcNote == null || etcNote.trim().isEmpty()) {
            return -1L; // 정보가 없으면 -1 반환 (제한 없음)
        }

        try {
            // 개선된 정규식으로 금액 추출
            // "1만원", "10만원", "100만원", "1,000원", "10,000원" 등을 모두 캐치
            Pattern pattern = Pattern.compile("([0-9,]+(?:만|천|백)?)[\\s]*원");
            Matcher matcher = pattern.matcher(etcNote);

            if (matcher.find()) {
                String amountStr = matcher.group(1);
                Long convertedAmount = convertKoreanNumberToLong(amountStr);

                if (convertedAmount > 0) {
                    return convertedAmount;
                }
            }
        } catch (Exception e) {
            log.warn("최소 금액 Long 추출 실패: etcNote={}, error={}", etcNote, e.getMessage());
        }

        return -1L; // 추출 실패 시 -1 반환 (제한 없음)
    }

    /**
     * 한국어 숫자를 Long 타입으로 변환 (계산용)
     * 예: "1만" -> 10000, "10만" -> 100000, "100만" -> 1000000, "1,000" -> 1000
     */
    private Long convertKoreanNumberToLong(String amount) {
        try {
            if (amount == null || amount.trim().isEmpty()) {
                return 0L;
            }

            // 쉼표 제거
            amount = amount.replace(",", "").trim();
            long result = 0L;

            if (amount.contains("만")) {
                // "만" 처리
                String[] parts = amount.split("만");
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    long manValue = Long.parseLong(parts[0]);
                    result = manValue * 10000;
                }

                // 만 뒤에 추가 숫자 처리 (예: "1만5천")
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    String remainder = parts[1];
                    if (remainder.contains("천")) {
                        String cheonStr = remainder.replace("천", "");
                        if (!cheonStr.isEmpty()) {
                            result += Long.parseLong(cheonStr) * 1000;
                        }
                    } else if (remainder.contains("백")) {
                        String baekStr = remainder.replace("백", "");
                        if (!baekStr.isEmpty()) {
                            result += Long.parseLong(baekStr) * 100;
                        }
                    } else {
                        // 만 뒤에 순수 숫자가 있는 경우
                        if (!remainder.isEmpty()) {
                            result += Long.parseLong(remainder);
                        }
                    }
                }
            } else if (amount.contains("천")) {
                // "천" 단위만 있는 경우
                String cheonStr = amount.replace("천", "");
                if (!cheonStr.isEmpty()) {
                    result = Long.parseLong(cheonStr) * 1000;
                }
            } else if (amount.contains("백")) {
                // "백" 단위만 있는 경우
                String baekStr = amount.replace("백", "");
                if (!baekStr.isEmpty()) {
                    result = Long.parseLong(baekStr) * 100;
                }
            } else {
                // 순수 숫자인 경우
                result = Long.parseLong(amount);
            }

            return result;

        } catch (NumberFormatException e) {
            log.warn("한국어 숫자 변환 실패: amount={}, error={}", amount, e.getMessage());
            return 0L; // 변환 실패 시 0L 반환
        }
    }
}