package team2.pjt12.matchumoney.domain.deposit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.deposit.domain.UserDepositVO;
import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.UserDepositResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.mapper.DepositProductMapper;
import team2.pjt12.matchumoney.domain.deposit.mapper.UserDepositMapper;
import team2.pjt12.matchumoney.domain.deposit.util.AmountExtractorUtil; // 🆕 추가

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDepositServiceImpl implements UserDepositService {
    private final UserDepositMapper userDepositMapper;
    private final DepositProductMapper depositProductMapper;

    @Override
    public List<UserDepositResponseDTO> getAccountsByUserId(String userId) {
        List<UserDepositVO> userDepositVOList = userDepositMapper.getAccountsByUserId(userId);

        // 계좌가 없는 사용자 처리
        if (userDepositVOList.isEmpty()) {
            log.warn("계좌가 없는 사용자: userId={}", userId);
        }

        // VO -> DTO 변환
        List<UserDepositResponseDTO> userDepositResponseDTOList = userDepositVOList.stream()
                .map(UserDepositResponseDTO::from)
                .collect(Collectors.toList());

        return userDepositResponseDTOList;
    }

    @Override
    public List<DepositProductResponseDTO> getProductsByBalance(BalanceRequestDTO request) {
        log.info("잔액 기반 상품 추천 시작: userId={}, balance={}", request.getUserId(), request.getBalance());

        // 1. 모든 상품 조회
        List<DepositProductResponseDTO> allProducts = depositProductMapper.findAllDepositProducts();

        // 2. 각 상품의 최소 금액 추출 및 잔액과 비교하여 필터링
        List<DepositProductResponseDTO> filteredProducts = allProducts.stream()
                .filter(product -> {
// 🔍 etcNote 원본 출력
                    log.info("=== 상품 분석 시작 ===");
                    log.info("상품명: {}", product.getProductName());
                    log.info("etcNote 원본: '{}'", product.getEtcNote());

                    // 최소 금액 추출
                    Long minAmount = extractMinAmountAsLong(product.getEtcNote());
                    log.info("추출된 최소금액: {}원", minAmount);

                    // 사용자 잔액이 최소 금액 이상인 경우만 포함
                    boolean canAfford = request.getBalance() >= minAmount;

                    log.info("사용자잔액: {}원", request.getBalance());
                    log.info("비교: {} >= {} = {}", request.getBalance(), minAmount, canAfford);
                    log.info("=== 상품 분석 종료 ===");

                    return canAfford;
                })
                .collect(Collectors.toList());

        // 3. 각 상품의 최소 금액을 문자열로 설정 (화면 표시용) - 🔧 유틸리티 사용
        filteredProducts.forEach(product -> {
            String minAmount = AmountExtractorUtil.extractMinAmount(product.getEtcNote());
            product.setMinAmount(minAmount);
        });

        log.info("잔액 기반 필터링 완료: 전체 {}개 상품 중 {}개 상품이 가입 가능",
                allProducts.size(), filteredProducts.size());

        return filteredProducts;
    }

    /**
     * etcNote에서 최소 금액을 추출하여 Long 타입으로 반환 (계산용)
     */
    private Long extractMinAmountAsLong(String etcNote) {
        if (etcNote == null || etcNote.trim().isEmpty()) {
            return 0L; // 정보가 없으면 0원으로 처리 (모든 잔액에서 가입 가능)
        }

        // 정규식으로 금액 추출 (예: "1만원", "10만원", "100만원" 등)
        Pattern pattern = Pattern.compile("([0-9]+(?:[천백십]*[만]?)+)원");
        Matcher matcher = pattern.matcher(etcNote);

        if (matcher.find()) {
            String amountStr = matcher.group(1);
            return convertKoreanNumberToLong(amountStr);
        } else {
            // 일반 숫자만 있는 경우 처리 (예: "1000원", "10000원")
            Pattern numberPattern = Pattern.compile("([0-9]+)원");
            Matcher numberMatcher = numberPattern.matcher(etcNote);
            if (numberMatcher.find()) {
                return Long.parseLong(numberMatcher.group(1));
            }
        }

        return 0L; // 추출 실패 시 0원으로 처리
    }

    // 🗑️ extractMinAmount, convertKoreanNumber 메서드 제거 (유틸리티 사용)

    /**
     * 한국어 숫자를 Long 타입으로 변환 (계산용)
     * 예: "1만" -> 10000, "10만" -> 100000, "100만" -> 1000000
     */
    private Long convertKoreanNumberToLong(String amount) {
        try {
            long result = 0L;

            // "만" 단위 처리
            if (amount.contains("만")) {
                String[] parts = amount.split("만");
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    long manValue = Long.parseLong(parts[0]);
                    result += manValue * 10000;
                }

                // 만 뒤에 추가 숫자가 있는 경우 (예: "1만5천")
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
                    }
                }
            }
            // "천" 단위만 있는 경우
            else if (amount.contains("천")) {
                String cheonStr = amount.replace("천", "");
                if (!cheonStr.isEmpty()) {
                    result = Long.parseLong(cheonStr) * 1000;
                }
            }
            // "백" 단위만 있는 경우
            else if (amount.contains("백")) {
                String baekStr = amount.replace("백", "");
                if (!baekStr.isEmpty()) {
                    result = Long.parseLong(baekStr) * 100;
                }
            }
            // 순수 숫자인 경우
            else {
                result = Long.parseLong(amount);
            }

            return result;

        } catch (NumberFormatException e) {
            log.warn("한국어 숫자 변환 실패: {}", amount, e);
            return 0L;
        }
    }


}