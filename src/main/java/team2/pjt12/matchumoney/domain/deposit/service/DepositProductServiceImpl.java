package team2.pjt12.matchumoney.domain.deposit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.mapper.DepositProductMapper;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositProductServiceImpl implements DepositProductService {

    private final DepositProductMapper depositProductMapper;
    private final UserMapper userMapper;

    private static final Pattern AMOUNT_PATTERN = Pattern.compile("([0-9]+)([십백천만억]?)만원");
    private static final Map<String, Integer> UNIT_MULTIPLIERS = Map.of(
            "십", 10,
            "백", 100,
            "천", 1_000,
            "만", 10_000,
            "억", 100_000_000
    );

    private static final long[] UNITS = {100_000_000L, 10_000L, 1_000L, 100L};
    private static final String[] UNIT_NAMES = {"억", "만", "천", "백"};


    @Override
    @Transactional(readOnly = true)
    public List<DepositProductResponseDTO> getAllDepositProducts() {
        List<DepositProductResponseDTO> products = depositProductMapper.findAllDepositProducts();
        products.forEach(this::setMinAmountForDisplay);
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
        products.forEach(this::setMinAmountForDisplay);

        log.info("{}의 예금 상품 수: {}", bankName, products.size());
        return products;
    }

    @Override
    public List<DepositProductResponseDTO> getProductsByBalance(BalanceRequestDTO request) {
        log.info("잔액 기반 상품 추천 시작: userId={}, balance={}", request.getUserId(), request.getBalance());

        try {
            validateRequest(request);

            List<DepositProductResponseDTO> allProducts = depositProductMapper.findAllDepositProducts();
            log.info("전체 상품 수: {}", allProducts.size());

            List<DepositProductResponseDTO> filteredProducts = allProducts.stream()
                    .filter(product -> isProductAvailableForBalance(product, request.getBalance()))
                    .collect(Collectors.toList());

            filteredProducts.forEach(this::setMinAmountForDisplay);

            log.info("필터링 결과: 사용자 잔액 {}원으로 가입 가능한 상품 {}개 (전체: {}개)",
                    request.getBalance(), filteredProducts.size(), allProducts.size());

            return filteredProducts;

        } catch (Exception e) {
            log.error("getProductsByBalance 실행 중 오류", e);
            throw new RuntimeException("상품 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void validateRequest(BalanceRequestDTO request) {
        if (request.getUserId() == null || request.getBalance() == null) {
            log.error("필수 파라미터 누락: userId={}, balance={}", request.getUserId(), request.getBalance());
            throw new IllegalArgumentException("userId와 balance는 필수입니다");
        }
    }

    private boolean isProductAvailableForBalance(DepositProductResponseDTO product, Long userBalance) {
        try {
            Long minAmount = extractMinAmountAsLong(product.getEtcNote());

            // 최소 금액 정보가 없으면 가입 가능으로 처리
            if (minAmount == -1L) {
                log.debug("상품 '{}' - 최소 금액 정보 없음, 가입 가능으로 처리", product.getProductName());
                return true;
            }

            boolean available = userBalance >= minAmount;
            log.debug("상품 '{}' - 사용자 잔액: {}원, 최소 금액: {}원, 가입 가능: {}",
                    product.getProductName(), userBalance, minAmount, available);

            return available;
        } catch (Exception e) {
            log.warn("상품 '{}' 필터링 중 오류: {}, 가입 가능으로 처리", product.getProductName(), e.getMessage());
            return true;
        }
    }

    private void setMinAmountForDisplay(DepositProductResponseDTO product) {
        String minAmount = extractMinAmountAsString(product.getEtcNote());
        product.setMinAmount(minAmount);
    }

    /**
     * etcNote에서 최소 금액을 추출하여 한국어 형태로 반환
     */
    private String extractMinAmountAsString(String etcNote) {
        if (etcNote == null || etcNote.trim().isEmpty()) {
            return "null";
        }

        try {
            Matcher matcher = AMOUNT_PATTERN.matcher(etcNote);
            if (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);
                int multiplier = UNIT_MULTIPLIERS.getOrDefault(unit, 1);
                long amount = (long) number * multiplier * 10_000;

                return formatKoreanAmount(amount);
            }
        } catch (NumberFormatException e) {
            log.warn("최소 금액 문자열 추출 실패: {}", e.getMessage());
        }

        return "제한 없음";
    }

    /**
     * 금액을 한국어 단위로 변환 (예: 1000000 → 100만원)
     */
    private String formatKoreanAmount(long amount) {
        if (amount == 0) return "0원";

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < UNITS.length; i++) {
            long unitValue = amount / UNITS[i];
            if (unitValue > 0) {
                result.append(unitValue).append(UNIT_NAMES[i]);
                amount %= UNITS[i];
            }
        }

        if (amount > 0) {
            result.append(amount);
        }

        return result.append("원").toString();
    }

    /**
     * etcNote에서 최소 금액을 추출하여 Long 타입으로 반환 (계산용)
     */
    private Long extractMinAmountAsLong(String etcNote) {
        if (etcNote == null || etcNote.trim().isEmpty()) {
            log.debug("etcNote가 비어있음");
            return -1L;
        }

        log.debug("원본 etcNote: '{}'", etcNote);

        // 기존 패턴으로 먼저 시도
        Matcher matcher = AMOUNT_PATTERN.matcher(etcNote);
        if (matcher.find()) {
            try {
                int number = Integer.parseInt(matcher.group(1));
                String unit = matcher.group(2);
                int multiplier = UNIT_MULTIPLIERS.getOrDefault(unit, 1);
                long amount = (long) number * multiplier * 10_000;

                log.debug("AMOUNT_PATTERN 매칭: number={}, unit='{}', multiplier={}, 최종금액={}원",
                        number, unit, multiplier, amount);
                return amount;
            } catch (NumberFormatException e) {
                log.warn("AMOUNT_PATTERN 숫자 파싱 실패: {}", e.getMessage());
            }
        }

        // 대안 패턴으로 시도
        Pattern alternativePattern = Pattern.compile("([0-9,]+(?:만|천|백)?)[\\s]*원");
        Matcher altMatcher = alternativePattern.matcher(etcNote);

        if (altMatcher.find()) {
            String amountStr = altMatcher.group(1);
            log.debug("대안 패턴 매칭: amountStr='{}'", amountStr);

            Long convertedAmount = convertKoreanNumberToLong(amountStr);
            log.debug("변환된 금액: {}원", convertedAmount);

            return convertedAmount > 0 ? convertedAmount : -1L;
        }

        log.debug("금액 패턴 매칭 실패");
        return -1L;
    }

    /**
     * 한국어 숫자를 Long 타입으로 변환 (계산용)
     */
    private Long convertKoreanNumberToLong(String amount) {
        try {
            if (amount == null || amount.trim().isEmpty()) {
                return 0L;
            }

            log.debug("변환 시작: '{}'", amount);
            amount = amount.replace(",", "").trim();
            long result = 0L;

            if (amount.contains("만")) {
                String[] parts = amount.split("만");
                log.debug("만 단위 분리: {}", java.util.Arrays.toString(parts));

                if (parts.length > 0 && !parts[0].isEmpty()) {
                    long manValue = Long.parseLong(parts[0]);
                    result = manValue * 10000;
                    log.debug("만 단위: {} -> {}원", manValue, result);
                }

                if (parts.length > 1 && !parts[1].isEmpty()) {
                    long subAmount = parseSubUnit(parts[1]);
                    result += subAmount;
                    log.debug("하위 단위: {} -> 총 {}원", subAmount, result);
                }
            } else if (amount.contains("천")) {
                String cheonStr = amount.replace("천", "");
                if (!cheonStr.isEmpty()) {
                    result = Long.parseLong(cheonStr) * 1000;
                    log.debug("천 단위: {} -> {}원", cheonStr, result);
                }
            } else if (amount.contains("백")) {
                String baekStr = amount.replace("백", "");
                if (!baekStr.isEmpty()) {
                    result = Long.parseLong(baekStr) * 100;
                    log.debug("백 단위: {} -> {}원", baekStr, result);
                }
            } else {
                result = Long.parseLong(amount);
                log.debug("순수 숫자: {} -> {}원", amount, result);
            }

            log.debug("최종 변환 결과: {}원", result);
            return result;

        } catch (NumberFormatException e) {
            log.warn("한국어 숫자 변환 실패: amount='{}', error={}", amount, e.getMessage());
            return 0L;
        }
    }

    private long parseSubUnit(String remainder) {
        if (remainder.contains("천")) {
            String cheonStr = remainder.replace("천", "");
            return !cheonStr.isEmpty() ? Long.parseLong(cheonStr) * 1000 : 0;
        } else if (remainder.contains("백")) {
            String baekStr = remainder.replace("백", "");
            return !baekStr.isEmpty() ? Long.parseLong(baekStr) * 100 : 0;
        } else if (!remainder.isEmpty()) {
            return Long.parseLong(remainder);
        }
        return 0;
    }
    @Transactional(readOnly = true)
    public List<DepositProductResponseDTO> getAllDepositProductsWithFavorites(Long userId) {
        List<DepositProductResponseDTO> products = depositProductMapper.findAllDepositProducts();

        for (DepositProductResponseDTO product : products) {
            // 사용자별 즐겨찾기 여부 확인
            boolean isFavorite = userMapper.isDepositFavoriteExists(userId, product.getDepositProductId());
            product.setFavorite(isFavorite);

            // 기존 로직도 유지 (최소 금액 추출)
            setMinAmountForDisplay(product);
        }

        return products;
    }

}