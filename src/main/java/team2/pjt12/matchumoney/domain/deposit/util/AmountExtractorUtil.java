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
        if (etcNote == null || etcNote.trim().isEmpty()) {
            log.debug("etcNote가 비어있음");
            return "정보 없음";
        }

        log.debug("AmountExtractorUtil - etcNote 파싱: '{}'", etcNote);

        // 1. 한국어 단위 패턴 (1만원, 10만원, 100만원)
        Pattern koreanPattern = Pattern.compile("([0-9]+(?:[천백십]*[만]?)+원)");
        Matcher koreanMatcher = koreanPattern.matcher(etcNote);

        if (koreanMatcher.find()) {
            String amount = koreanMatcher.group(1);
            String converted = convertKoreanNumber(amount);
            log.debug("한국어 패턴 매칭: '{}' -> '{}'", amount, converted);
            return converted;
        }

        // 2. 쉼표 포함 패턴 (1,000,000원)
        Pattern commaPattern = Pattern.compile("([0-9,]+원)");
        Matcher commaMatcher = commaPattern.matcher(etcNote);

        if (commaMatcher.find()) {
            String amount = commaMatcher.group(1);
            log.debug("쉼표 패턴 매칭: '{}'", amount);
            return amount;
        }

        // 3. 일반 숫자 패턴 (1000원)
        Pattern numberPattern = Pattern.compile("([0-9]+원)");
        Matcher numberMatcher = numberPattern.matcher(etcNote);

        if (numberMatcher.find()) {
            String amount = numberMatcher.group(1);
            log.debug("숫자 패턴 매칭: '{}'", amount);
            return amount;
        }

        log.warn("모든 패턴 매칭 실패: '{}'", etcNote);
        return "정보 없음";
    }

    /**
     * 한국어 숫자 단위를 숫자로 변환
     */
    private static String convertKoreanNumber(String amount) {
        return amount.replace("천", "000")
                .replace("백", "00")
                .replace("십", "0");
    }
}