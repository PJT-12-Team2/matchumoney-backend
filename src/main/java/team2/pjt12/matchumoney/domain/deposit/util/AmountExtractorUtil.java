package team2.pjt12.matchumoney.domain.deposit.util;

import lombok.extern.slf4j.Slf4j;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AmountExtractorUtil {

    /**
     * etcNote에서 최소 금액을 추출하여 문자열로 반환 (화면 표시용)
     */
    public static String extractMinAmount(String etcNote) {
        Long amountLong = extractMinAmountAsLong(etcNote);

        if (amountLong == -1L) {
            return "정보없음"; // 🔧 추출 실패한 경우
        } else if (amountLong == 0L) {
            return "0원"; // 🔧 추출은 됐지만 0원인 경우
        }

        return formatAmount(amountLong);
    }

    /**
     * etcNote에서 최소 금액을 추출하여 Long 타입으로 반환 (계산용)
     */
    public static Long extractMinAmountAsLong(String etcNote) {
        if (etcNote == null || etcNote.trim().isEmpty()) {
            return -1L; // 🔧 정보가 없으면 -1 반환
        }

        log.debug("=== 최소금액 추출 시작 ===");
        log.debug("원본 etcNote: '{}'", etcNote);

        // 정규식으로 금액 추출
        Pattern pattern = Pattern.compile("([0-9,]+(?:만|천|백)?)[\\s]*원");
        Matcher matcher = pattern.matcher(etcNote);

        if (matcher.find()) {
            String amountStr = matcher.group(1);
            log.debug("추출된 금액 문자열: '{}'", amountStr);

            Long convertedAmount = convertKoreanNumberToLong(amountStr);

            if (convertedAmount > 0) {
                log.debug("변환된 금액: {}원", convertedAmount);
                return convertedAmount;
            }
        }

        log.debug("금액 패턴을 찾을 수 없음: '{}'", etcNote);
        return -1L; // 🔧 추출 실패 시 -1 반환
    }

    /**
     * 한국어 숫자를 Long 타입으로 변환 (계산용)
     * 예: "1만" -> 10000, "10만" -> 100000, "100만" -> 1000000, "1,000" -> 1000
     */
    private static Long convertKoreanNumberToLong(String amount) {
        try {
            log.debug("숫자 변환 시작: '{}'", amount);

            // 쉼표 제거
            amount = amount.replace(",", "");

            long result = 0L;

            if (amount.contains("만")) {
                // "만" 처리
                String[] parts = amount.split("만");
                if (parts.length > 0 && !parts[0].isEmpty()) {
                    long manValue = Long.parseLong(parts[0]);
                    result = manValue * 10000;
                    log.debug("만 단위 변환: {} -> {}", parts[0], result);
                }

                // 만 뒤에 추가 숫자 처리 (예: "1만5천")
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    String remainder = parts[1];
                    if (remainder.contains("천")) {
                        String cheonStr = remainder.replace("천", "");
                        if (!cheonStr.isEmpty()) {
                            result += Long.parseLong(cheonStr) * 1000;
                            log.debug("천 단위 추가: {} -> 총 {}", cheonStr, result);
                        }
                    } else if (remainder.contains("백")) {
                        String baekStr = remainder.replace("백", "");
                        if (!baekStr.isEmpty()) {
                            result += Long.parseLong(baekStr) * 100;
                            log.debug("백 단위 추가: {} -> 총 {}", baekStr, result);
                        }
                    } else {
                        // 만 뒤에 순수 숫자가 있는 경우
                        if (!remainder.isEmpty()) {
                            result += Long.parseLong(remainder);
                            log.debug("단위 추가: {} -> 총 {}", remainder, result);
                        }
                    }
                }
            } else if (amount.contains("천")) {
                // "천" 단위만 있는 경우
                String cheonStr = amount.replace("천", "");
                if (!cheonStr.isEmpty()) {
                    result = Long.parseLong(cheonStr) * 1000;
                    log.debug("천 단위 변환: {} -> {}", cheonStr, result);
                }
            } else if (amount.contains("백")) {
                // "백" 단위만 있는 경우
                String baekStr = amount.replace("백", "");
                if (!baekStr.isEmpty()) {
                    result = Long.parseLong(baekStr) * 100;
                    log.debug("백 단위 변환: {} -> {}", baekStr, result);
                }
            } else {
                // 순수 숫자인 경우
                result = Long.parseLong(amount);
                log.debug("순수 숫자 변환: {} -> {}", amount, result);
            }

            log.debug("최종 변환 결과: {}원", result);
            return result;

        } catch (NumberFormatException e) {
            log.warn("한국어 숫자 변환 실패: '{}', 오류: {}", amount, e.getMessage());
            return 0L;
        }
    }

    /**
     * Long 금액을 사용자 친화적 문자열로 포맷팅
     * 예: 10000 -> "1만원", 100000 -> "10만원", 1500 -> "1,500원"
     */
    private static String formatAmount(Long amount) {
        if (amount >= 10000) {
            if (amount % 10000 == 0) {
                return (amount / 10000) + "만원";
            } else {
                return String.format("%,d원", amount);
            }
        } else if (amount >= 1000) {
            if (amount % 1000 == 0) {
                return (amount / 1000) + "천원";
            } else {
                return String.format("%,d원", amount);
            }
        } else {
            return amount + "원";
        }
    }

    /**
     * 한국어 숫자 단위를 숫자로 변환 (기존 메소드 - 사용하지 않음)
     */
    @Deprecated
    private static String convertKoreanNumber(String amount) {
        return amount.replace("천", "000")
                .replace("백", "00")
                .replace("십", "0");
    }
}