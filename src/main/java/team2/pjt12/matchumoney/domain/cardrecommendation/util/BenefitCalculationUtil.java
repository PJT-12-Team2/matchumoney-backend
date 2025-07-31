package team2.pjt12.matchumoney.domain.cardrecommendation.util;

import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardParsedBenefitVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardTransactionSummaryVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BenefitCalculationUtil {
    
    /**
     * 특정 혜택과 거래 내역을 기반으로 혜택 금액을 계산합니다.
     * @param benefit 카드 혜택 정보
     * @param transactionSummary 카테고리별 거래 요약
     * @param previousMonthSpend 전월 총 사용액
     * @return 계산된 혜택 금액
     */
    public static BigDecimal calculateBenefitAmount(
            CardParsedBenefitVO benefit, 
            CardTransactionSummaryVO transactionSummary,
            Long previousMonthSpend) {
        
        // 카테고리 매칭 확인 (정확한 매칭 및 유사 카테고리 매칭)
        if (!isCategoryMatched(benefit.getCategory(), transactionSummary.getCategory())) {
            return BigDecimal.ZERO;
        }
        
        // 개별 혜택별 전월 실적 조건 확인
        Integer requiredPreMonthMoney = benefit.getPreMonthMoneySpecific();
        if (requiredPreMonthMoney != null && previousMonthSpend < requiredPreMonthMoney) {
            return BigDecimal.ZERO;
        }
        
        // 건당 최소 결제 금액 조건 확인
        if (benefit.getMinSpendPerTransaction() != null) {
            BigDecimal avgAmount = transactionSummary.getAverageAmount();
            if (avgAmount == null || avgAmount.compareTo(BigDecimal.valueOf(benefit.getMinSpendPerTransaction())) < 0) {
                return BigDecimal.ZERO;
            }
        }
        
        BigDecimal benefitAmount = BigDecimal.ZERO;
        BigDecimal transactionAmount = BigDecimal.valueOf(transactionSummary.getTotalAmount());
        
        // 혜택 유형에 따른 정확한 계산
        String benefitType = benefit.getBenefitType();
        if ("할인".equals(benefitType) || "적립".equals(benefitType) || "캐시백".equals(benefitType) || "페이백".equals(benefitType)) {
            
            BigDecimal benefitValue = benefit.getValue();
            if (benefitValue == null || benefitValue.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }
            
            // 퍼센트 혜택 vs 고정 금액 혜택 구분 개선
            String conditionText = benefit.getConditionText();
            boolean isPercentageBenefit = isPercentageBenefit(benefitValue, conditionText, benefit.getTitle());
            
            if (isPercentageBenefit) {
                // 퍼센트 혜택 계산
                benefitAmount = transactionAmount
                    .multiply(benefitValue)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                // 고정 금액 혜택 계산
                if (conditionText != null && conditionText.contains("건당")) {
                    // 건당 고정 혜택: 혜택금액 × 거래건수
                    benefitAmount = benefitValue
                        .multiply(BigDecimal.valueOf(transactionSummary.getTransactionCount()));
                } else if (conditionText != null && (conditionText.contains("월") || conditionText.contains("매월"))) {
                    // 월 단위 고정 혜택: 조건 만족 시 한 번만 지급
                    benefitAmount = benefitValue;
                } else {
                    // 사용액 기준 고정 혜택: 일정 사용액당 혜택
                    // 예: 10만원당 5천원 -> (총사용액 / 10만원) * 5천원
                    if (conditionText != null && conditionText.contains("만원당")) {
                        String[] parts = conditionText.split("만원당");
                        if (parts.length > 0) {
                            try {
                                int baseAmount = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                                if (baseAmount > 0) {
                                    int timesEligible = (int) (transactionAmount.longValue() / (baseAmount * 10000));
                                    benefitAmount = benefitValue.multiply(BigDecimal.valueOf(timesEligible));
                                } else {
                                    benefitAmount = benefitValue;
                                }
                            } catch (NumberFormatException e) {
                                benefitAmount = benefitValue;
                            }
                        } else {
                            benefitAmount = benefitValue;
                        }
                    } else {
                        // 기본 고정 혜택
                        benefitAmount = benefitValue;
                    }
                }
            }
        }
        
        // 월 최대 혜택 한도 적용
        if (benefit.getMaxBenefitMonthly() != null && benefit.getMaxBenefitMonthly() > 0) {
            BigDecimal maxBenefit = BigDecimal.valueOf(benefit.getMaxBenefitMonthly());
            if (benefitAmount.compareTo(maxBenefit) > 0) {
                benefitAmount = maxBenefit;
            }
        }
        
        return benefitAmount.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 혜택이 퍼센트 혜택인지 고정 금액 혜택인지 판단합니다.
     * @param benefitValue 혜택 값
     * @param conditionText 조건 텍스트
     * @param title 혜택 제목
     * @return 퍼센트 혜택 여부
     */
    private static boolean isPercentageBenefit(BigDecimal benefitValue, String conditionText, String title) {
        // 조건 텍스트나 제목에 '%' 포함 시 퍼센트 혜택
        if ((conditionText != null && conditionText.contains("%")) || 
            (title != null && title.contains("%"))) {
            return true;
        }
        
        // 조건 텍스트에 '원' 포함 시 고정 금액 혜택
        if (conditionText != null && conditionText.contains("원")) {
            return false;
        }
        
        // 혜택 값이 100 이하이고 소수점이 있으면 퍼센트일 가능성 높음
        if (benefitValue.compareTo(BigDecimal.valueOf(100)) <= 0 && 
            benefitValue.scale() > 0) {
            return true;
        }
        
        // 혜택 값이 10 이하면 퍼센트로 가정 (일반적인 카드 혜택율)
        if (benefitValue.compareTo(BigDecimal.valueOf(10)) <= 0) {
            return true;
        }
        
        // 그 외는 고정 금액으로 가정
        return false;
    }
    
    /**
     * 카드의 모든 혜택을 기반으로 총 혜택 금액을 계산합니다.
     * @param benefits 카드의 모든 혜택 목록
     * @param transactionSummaries 카테고리별 거래 요약 목록
     * @param previousMonthSpend 전월 총 사용액
     * @param cardPreMonthMoney 카드 전체 전월 실적 조건
     * @return 총 혜택 금액
     */
    public static BigDecimal calculateTotalBenefit(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long previousMonthSpend,
            Long cardPreMonthMoney) {
        
        // 카드 전체 전월 실적 조건 확인
        if (cardPreMonthMoney != null && cardPreMonthMoney > 0 && previousMonthSpend < cardPreMonthMoney) {
            // 전월 실적 미달 시에도 최소 혜택은 계산 (실적 조건 없는 혜택들)
            BigDecimal minBenefit = BigDecimal.ZERO;
            for (CardParsedBenefitVO benefit : benefits) {
                if (benefit.getPreMonthMoneySpecific() == null || benefit.getPreMonthMoneySpecific() <= 0) {
                    for (CardTransactionSummaryVO summary : transactionSummaries) {
                        BigDecimal benefitAmount = calculateBenefitAmount(benefit, summary, previousMonthSpend);
                        minBenefit = minBenefit.add(benefitAmount);
                    }
                }
            }
            return minBenefit.setScale(0, RoundingMode.HALF_UP);
        }
        
        BigDecimal totalBenefit = BigDecimal.ZERO;
        
        // 카테고리별로 최고 혜택만 적용 (중복 혜택 방지)
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            BigDecimal maxCategoryBenefit = BigDecimal.ZERO;
            
            for (CardParsedBenefitVO benefit : benefits) {
                BigDecimal benefitAmount = calculateBenefitAmount(benefit, summary, previousMonthSpend);
                if (benefitAmount.compareTo(maxCategoryBenefit) > 0) {
                    maxCategoryBenefit = benefitAmount;
                }
            }
            
            totalBenefit = totalBenefit.add(maxCategoryBenefit);
        }
        
        return totalBenefit.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 날짜가 주말인지 확인합니다.
     * @param dateString 날짜 문자열 (YYYYMMDD)
     * @return 주말 여부
     */
    public static boolean isWeekend(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 조건 텍스트를 파싱하여 해당 조건을 만족하는지 확인합니다.
     * @param conditionText 조건 텍스트 (콤마로 구분)
     * @param category 거래 카테고리
     * @param transactionDate 거래 날짜
     * @return 조건 만족 여부
     */
    public static boolean checkCondition(String conditionText, String category, String transactionDate) {
        if (conditionText == null || conditionText.trim().isEmpty()) {
            return true; // 조건이 없으면 통과
        }
        
        String[] conditions = conditionText.split(",");
        
        for (String condition : conditions) {
            condition = condition.trim().toLowerCase();
            
            if ("주말".equals(condition) && !isWeekend(transactionDate)) {
                return false;
            }
            if ("평일".equals(condition) && isWeekend(transactionDate)) {
                return false;
            }
            // 온라인/오프라인 조건은 카테고리 기반으로 판단
            if ("온라인".equals(condition) && !isOnlineCategory(category)) {
                return false;
            }
            if ("오프라인".equals(condition) && isOnlineCategory(category)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 카테고리가 온라인 관련인지 확인합니다.
     * @param category 카테고리명
     * @return 온라인 카테고리 여부
     */
    private static boolean isOnlineCategory(String category) {
        return "온라인쇼핑".equals(category) || "온라인서비스".equals(category);
    }
    
    /**
     * 혜택 카테고리와 거래 카테고리가 매칭되는지 확인합니다.
     * @param benefitCategory 혜택 카테고리
     * @param transactionCategory 거래 카테고리
     * @return 매칭 여부
     */
    private static boolean isCategoryMatched(String benefitCategory, String transactionCategory) {
        if (benefitCategory == null || transactionCategory == null) {
            return false;
        }
        
        // 정확한 매칭
        if (benefitCategory.equals(transactionCategory)) {
            return true;
        }
        
        // 유사 카테고리 매칭
        return matchSimilarCategories(benefitCategory, transactionCategory);
    }
    
    /**
     * 유사한 카테고리들을 매칭합니다.
     * @param benefitCategory 혜택 카테고리
     * @param transactionCategory 거래 카테고리
     * @return 유사 카테고리 매칭 여부
     */
    private static boolean matchSimilarCategories(String benefitCategory, String transactionCategory) {
        // 카테고리를 소문자로 변환하여 비교
        String benefit = benefitCategory.toLowerCase();
        String transaction = transactionCategory.toLowerCase();
        
        // 카페/디저트/커피 관련
        if ((benefit.contains("카페") || benefit.contains("커피") || benefit.contains("스타벅스") || benefit.contains("카페테리아")) && 
            (transaction.contains("카페") || transaction.contains("커피") || transaction.contains("디저트") || 
             transaction.contains("베이커리") || transaction.contains("도넛") || transaction.contains("스타벅스"))) {
            return true;
        }
        
        // 마트/편의점/생활용품 관련
        if ((benefit.contains("마트") || benefit.contains("편의점") || benefit.contains("생활") || benefit.contains("슈퍼")) && 
            (transaction.contains("마트") || transaction.contains("편의점") || transaction.contains("생활") || 
             transaction.contains("슈퍼") || transaction.contains("gs25") || transaction.contains("cu") || 
             transaction.contains("세븐일레븐") || transaction.contains("이마트") || transaction.contains("롯데마트"))) {
            return true;
        }
        
        // 주유/자동차 관련
        if ((benefit.contains("주유") || benefit.contains("기름") || benefit.contains("자동차") || benefit.contains("차량")) && 
            (transaction.contains("주유") || transaction.contains("기름") || transaction.contains("주유소") || 
             transaction.contains("sk에너지") || transaction.contains("gs칼텍스") || transaction.contains("s-oil") || 
             transaction.contains("현대오일뱅크") || transaction.contains("자동차"))) {
            return true;
        }
        
        // 대중교통/교통비 관련
        if ((benefit.contains("교통") || benefit.contains("지하철") || benefit.contains("버스") || benefit.contains("택시")) && 
            (transaction.contains("교통") || transaction.contains("지하철") || transaction.contains("버스") || 
             transaction.contains("택시") || transaction.contains("카카오택시") || transaction.contains("우버") || 
             transaction.contains("전철") || transaction.contains("ktx") || transaction.contains("기차"))) {
            return true;
        }
        
        // 온라인쇼핑/인터넷쇼핑 관련
        if ((benefit.contains("온라인") || benefit.contains("인터넷") || benefit.contains("쇼핑몰") || benefit.contains("온라인쇼핑")) && 
            (transaction.contains("온라인") || transaction.contains("인터넷") || transaction.contains("쇼핑") || 
             transaction.contains("11번가") || transaction.contains("g마켓") || transaction.contains("옥션") || 
             transaction.contains("쿠팡") || transaction.contains("위메프") || transaction.contains("티몬"))) {
            return true;
        }
        
        // 백화점/쇼핑/패션 관련
        if ((benefit.contains("백화점") || benefit.contains("쇼핑") || benefit.contains("패션") || benefit.contains("의류")) && 
            (transaction.contains("백화점") || transaction.contains("쇼핑") || transaction.contains("패션") || 
             transaction.contains("의류") || transaction.contains("신세계") || transaction.contains("롯데백화점") || 
             transaction.contains("현대백화점") || transaction.contains("갤러리아") || transaction.contains("아울렛"))) {
            return true;
        }
        
        // 통신비 관련
        if ((benefit.contains("통신") || benefit.contains("휴대폰") || benefit.contains("인터넷") || benefit.contains("통신비")) && 
            (transaction.contains("통신") || transaction.contains("휴대폰") || transaction.contains("인터넷") || 
             transaction.contains("skt") || transaction.contains("kt") || transaction.contains("lg유플러스") || 
             transaction.contains("olleh") || transaction.contains("티브로드"))) {
            return true;
        }
        
        // 병원/의료/약국 관련
        if ((benefit.contains("병원") || benefit.contains("의료") || benefit.contains("약국") || benefit.contains("헬스케어")) && 
            (transaction.contains("병원") || transaction.contains("의료") || transaction.contains("약국") || 
             transaction.contains("클리닉") || transaction.contains("치과") || transaction.contains("한의원") || 
             transaction.contains("약국"))) {
            return true;
        }
        
        // 외식/음식/레스토랑 관련
        if ((benefit.contains("외식") || benefit.contains("음식") || benefit.contains("레스토랑") || 
             benefit.contains("식당") || benefit.contains("맛집") || benefit.contains("요식업")) && 
            (transaction.contains("외식") || transaction.contains("음식") || transaction.contains("레스토랑") || 
             transaction.contains("식당") || transaction.contains("치킨") || transaction.contains("피자") || 
             transaction.contains("햄버거") || transaction.contains("분식") || transaction.contains("한식") || 
             transaction.contains("중식") || transaction.contains("일식") || transaction.contains("양식"))) {
            return true;
        }
        
        // 영화/문화/엔터테인먼트 관련
        if ((benefit.contains("영화") || benefit.contains("문화") || benefit.contains("엔터") || benefit.contains("여가")) && 
            (transaction.contains("영화") || transaction.contains("문화") || transaction.contains("cgv") || 
             transaction.contains("롯데시네마") || transaction.contains("메가박스") || transaction.contains("공연") || 
             transaction.contains("뮤지컬") || transaction.contains("콘서트"))) {
            return true;
        }
        
        // 항공/여행 관련
        if ((benefit.contains("항공") || benefit.contains("여행") || benefit.contains("호텔") || benefit.contains("숙박")) && 
            (transaction.contains("항공") || transaction.contains("여행") || transaction.contains("호텔") || 
             transaction.contains("숙박") || transaction.contains("에어") || transaction.contains("항공사") || 
             transaction.contains("모텔") || transaction.contains("펜션") || transaction.contains("리조트"))) {
            return true;
        }
        
        return false;
    }
}