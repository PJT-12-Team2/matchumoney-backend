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

        // 정규식으로 금액 추출
        Pattern pattern = Pattern.compile("([0-9,]+(?:만|천|백)?)[\\s]*원");
        Matcher matcher = pattern.matcher(etcNote);

        if (matcher.find()) {
            String amountStr = matcher.group(1);

            Long convertedAmount = convertKoreanNumberToLong(amountStr);

            if (convertedAmount > 0) {
                return convertedAmount;
            }
        }
        return -1L; // 🔧 추출 실패 시 -1 반환
    }

    /**
     * 한국어 숫자를 Long 타입으로 변환 (계산용)
     * 예: "1만" -> 10000, "10만" -> 100000, "100만" -> 1000000, "1,000" -> 1000
     */
    private static Long convertKoreanNumberToLong(String amount) {
        try {
            // 쉼표 제거
            amount = amount.replace(",", "");

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