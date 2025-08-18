package team2.pjt12.matchumoney.domain.cardrecommendation.util;

import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardTransactionSummaryVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardParsedBenefitVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardProductVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 개인화된 카드 추천을 위한 유틸리티 클래스
 * 사용자의 거래 패턴, 선호도, 라이프스타일을 분석하여 맞춤형 카드를 추천합니다.
 */
public class PersonalizedRecommendationUtil {
    
    /**
     * 사용자 프로파일 정보
     */
    public static class UserProfile {
        private Map<String, Double> categoryPreferences; // 카테고리별 선호도
        private Map<String, Integer> spendingPatterns;   // 카테고리별 월 평균 사용액
        private Map<String, Integer> transactionFrequency; // 카테고리별 거래 빈도
        private double totalMonthlySpending;             // 월 총 사용액
        private String lifestyleType;                    // 라이프스타일 유형
        private List<String> primaryCategories;         // 주요 사용 카테고리 (상위 3개)
        
        public UserProfile() {
            this.categoryPreferences = new HashMap<>();
            this.spendingPatterns = new HashMap<>();
            this.transactionFrequency = new HashMap<>();
            this.primaryCategories = new ArrayList<>();
        }
        
        // Getters and Setters
        public Map<String, Double> getCategoryPreferences() { return categoryPreferences; }
        public void setCategoryPreferences(Map<String, Double> categoryPreferences) { this.categoryPreferences = categoryPreferences; }
        
        public Map<String, Integer> getSpendingPatterns() { return spendingPatterns; }
        public void setSpendingPatterns(Map<String, Integer> spendingPatterns) { this.spendingPatterns = spendingPatterns; }
        
        public Map<String, Integer> getTransactionFrequency() { return transactionFrequency; }
        public void setTransactionFrequency(Map<String, Integer> transactionFrequency) { this.transactionFrequency = transactionFrequency; }
        
        public double getTotalMonthlySpending() { return totalMonthlySpending; }
        public void setTotalMonthlySpending(double totalMonthlySpending) { this.totalMonthlySpending = totalMonthlySpending; }
        
        public String getLifestyleType() { return lifestyleType; }
        public void setLifestyleType(String lifestyleType) { this.lifestyleType = lifestyleType; }
        
        public List<String> getPrimaryCategories() { return primaryCategories; }
        public void setPrimaryCategories(List<String> primaryCategories) { this.primaryCategories = primaryCategories; }
    }
    
    /**
     * 카드 추천 결과
     */
    public static class RecommendationResult {
        private String cardId;
        private String cardName;
        private double matchScore;           // 매칭 점수 (0-100)
        private BigDecimal expectedBenefit;  // 예상 혜택
        private double efficiencyScore;      // 효율성 점수
        private List<String> matchReasons;   // 추천 이유
        private Map<String, Object> details; // 상세 정보
        
        public RecommendationResult() {
            this.matchReasons = new ArrayList<>();
            this.details = new HashMap<>();
        }
        
        // Getters and Setters
        public String getCardId() { return cardId; }
        public void setCardId(String cardId) { this.cardId = cardId; }
        
        public String getCardName() { return cardName; }
        public void setCardName(String cardName) { this.cardName = cardName; }
        
        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
        
        public BigDecimal getExpectedBenefit() { return expectedBenefit; }
        public void setExpectedBenefit(BigDecimal expectedBenefit) { this.expectedBenefit = expectedBenefit; }
        
        public double getEfficiencyScore() { return efficiencyScore; }
        public void setEfficiencyScore(double efficiencyScore) { this.efficiencyScore = efficiencyScore; }
        
        public List<String> getMatchReasons() { return matchReasons; }
        public void setMatchReasons(List<String> matchReasons) { this.matchReasons = matchReasons; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }
    
    /**
     * 거래 내역으로부터 사용자 프로파일을 생성합니다.
     */
    public static UserProfile buildUserProfile(List<CardTransactionSummaryVO> transactionSummaries) {
        UserProfile profile = new UserProfile();
        
        if (transactionSummaries == null || transactionSummaries.isEmpty()) {
            return profile;
        }
        
        // 총 사용액 및 거래 건수 계산
        long totalAmount = transactionSummaries.stream()
            .mapToLong(CardTransactionSummaryVO::getTotalAmount)
            .sum();
        
        int totalTransactions = transactionSummaries.stream()
            .mapToInt(s -> s.getTransactionCount() != null ? s.getTransactionCount() : 0)
            .sum();
        
        profile.setTotalMonthlySpending(totalAmount);
        
        // 카테고리별 분석
        Map<String, Double> preferences = new HashMap<>();
        Map<String, Integer> spendingPatterns = new HashMap<>();
        Map<String, Integer> frequencies = new HashMap<>();
        
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            String category = summary.getCategory();
            
            // 선호도 = (카테고리 사용액 / 총 사용액) + (카테고리 거래 빈도 / 총 거래 빈도)
            double amountRatio = totalAmount > 0 ? (double) summary.getTotalAmount() / totalAmount : 0;
            double frequencyRatio = totalTransactions > 0 && summary.getTransactionCount() != null ? 
                (double) summary.getTransactionCount() / totalTransactions : 0;
            
            double preference = (amountRatio * 0.7) + (frequencyRatio * 0.3); // 사용액에 더 높은 가중치
            
            preferences.put(category, preference);
            spendingPatterns.put(category, Math.toIntExact(summary.getTotalAmount()));
            frequencies.put(category, summary.getTransactionCount() != null ? summary.getTransactionCount() : 0);
        }
        
        profile.setCategoryPreferences(preferences);
        profile.setSpendingPatterns(spendingPatterns);
        profile.setTransactionFrequency(frequencies);
        
        // 주요 카테고리 추출 (상위 3개)
        List<String> primaryCategories = preferences.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        profile.setPrimaryCategories(primaryCategories);
        
        // 라이프스타일 유형 결정
        String lifestyleType = determineLifestyleType(preferences, totalAmount);
        profile.setLifestyleType(lifestyleType);
        
        return profile;
    }
    
    /**
     * 라이프스타일 유형을 결정합니다.
     */
    private static String determineLifestyleType(Map<String, Double> preferences, long totalAmount) {
        // 주요 카테고리별 비중 분석
        double shoppingRatio = preferences.getOrDefault("쇼핑", 0.0);
        double travelRatio = preferences.getOrDefault("여행/숙박", 0.0);
        double foodRatio = preferences.getOrDefault("푸드", 0.0);
        double transportRatio = preferences.getOrDefault("교통", 0.0);
        double cultureRatio = preferences.getOrDefault("OTT/영화/문화", 0.0);
        double martRatio = preferences.getOrDefault("마트/편의점", 0.0);
        
        // 라이프스타일 유형 분류
        if (totalAmount >= 2000000) { // 월 200만원 이상
            if (travelRatio > 0.15) return "프리미엄_여행족";
            if (shoppingRatio > 0.20) return "프리미엄_쇼핑족";
            return "프리미엄_소비자";
        } else if (totalAmount >= 1000000) { // 월 100만원 이상
            if (foodRatio > 0.25) return "외식_선호형";
            if (transportRatio > 0.20) return "이동_활발형";
            if (cultureRatio > 0.15) return "문화_생활형";
            return "일반_소비자";
        } else { // 월 100만원 미만
            if (martRatio > 0.30) return "절약_실용형";
            if (transportRatio > 0.25) return "대중교통_이용형";
            return "기본_소비자";
        }
    }
    
    /**
     * 사용자 프로파일과 카드를 매칭하여 점수를 계산합니다.
     */
    public static double calculateCardMatchScore(
            UserProfile userProfile,
            List<CardParsedBenefitVO> cardBenefits,
            CardProductVO cardInfo) {
        
        if (userProfile == null || cardBenefits == null || cardBenefits.isEmpty()) {
            return 0.0;
        }
        
        double totalScore = 0.0;
        
        // 1. 카테고리 매칭 점수 (40점)
        double categoryScore = calculateCategoryMatchScore(userProfile, cardBenefits);
        totalScore += categoryScore * 0.4;
        
        // 2. 혜택 효율성 점수 (30점)
        double efficiencyScore = calculateBenefitEfficiencyScore(userProfile, cardBenefits);
        totalScore += efficiencyScore * 0.3;
        
        // 3. 사용 조건 적합성 점수 (20점)
        double conditionScore = calculateConditionCompatibilityScore(userProfile, cardInfo);
        totalScore += conditionScore * 0.2;
        
        // 4. 라이프스타일 적합성 점수 (10점)
        double lifestyleScore = calculateLifestyleCompatibilityScore(userProfile, cardBenefits);
        totalScore += lifestyleScore * 0.1;
        
        return Math.min(100.0, Math.max(0.0, totalScore));
    }
    
    /**
     * 카테고리 매칭 점수를 계산합니다.
     */
    private static double calculateCategoryMatchScore(UserProfile userProfile, List<CardParsedBenefitVO> cardBenefits) {
        Map<String, Double> userPreferences = userProfile.getCategoryPreferences();
        List<String> primaryCategories = userProfile.getPrimaryCategories();
        
        double score = 0.0;
        int matchedCategories = 0;
        
        // 사용자의 주요 카테고리별로 카드 혜택 매칭 확인
        for (String userCategory : primaryCategories) {
            double userPreference = userPreferences.getOrDefault(userCategory, 0.0);
            
            boolean hasMatchingBenefit = cardBenefits.stream()
                .filter(b -> !"유의사항".equals(b.getTitle()))
                .anyMatch(b -> CategoryMappingUtil.isCategoryMatch(b.getCategory(), userCategory));
            
            if (hasMatchingBenefit) {
                score += userPreference * 100; // 선호도에 비례한 점수
                matchedCategories++;
            }
        }
        
        // 매칭된 카테고리 수에 따른 보너스
        if (matchedCategories >= 2) score += 20;
        else if (matchedCategories >= 1) score += 10;
        
        return Math.min(100.0, score);
    }
    
    /**
     * 혜택 효율성 점수를 계산합니다.
     */
    private static double calculateBenefitEfficiencyScore(UserProfile userProfile, List<CardParsedBenefitVO> cardBenefits) {
        // 사용자의 사용 패턴에 따른 예상 혜택율 계산
        double totalExpectedBenefit = 0.0;
        double totalSpending = userProfile.getTotalMonthlySpending();
        
        if (totalSpending <= 0) return 0.0;
        
        Map<String, Integer> spendingPatterns = userProfile.getSpendingPatterns();
        
        for (Map.Entry<String, Integer> entry : spendingPatterns.entrySet()) {
            String category = entry.getKey();
            Integer monthlySpending = entry.getValue();
            
            if (monthlySpending <= 0) continue;
            
            // 해당 카테고리에 적용 가능한 최고 혜택률 찾기
            double maxBenefitRate = cardBenefits.stream()
                .filter(b -> !"유의사항".equals(b.getTitle()))
                .filter(b -> CategoryMappingUtil.isCategoryMatch(b.getCategory(), category))
                .mapToDouble(b -> calculateBenefitRate(b, monthlySpending))
                .max()
                .orElse(0.0);
            
            totalExpectedBenefit += monthlySpending * (maxBenefitRate / 100);
        }
        
        // 전체 혜택률 계산
        double overallBenefitRate = (totalExpectedBenefit / totalSpending) * 100;
        
        // 혜택률을 점수로 변환 (2% 혜택률이면 100점)
        return Math.min(100.0, overallBenefitRate * 50);
    }
    
    /**
     * 특정 혜택의 혜택률을 계산합니다.
     */
    private static double calculateBenefitRate(CardParsedBenefitVO benefit, int monthlySpending) {
        BigDecimal benefitValue = benefit.getValue();
        if (benefitValue == null || benefitValue.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        
        String conditionText = benefit.getConditionText();
        boolean isPercentage = isPercentageBenefit(benefitValue, conditionText, benefit.getTitle());
        
        if (isPercentage) {
            // 월 한도 고려
            Integer maxMonthly = benefit.getMaxBenefitMonthly();
            if (maxMonthly != null && maxMonthly > 0) {
                double maxPossibleBenefit = maxMonthly;
                double calculatedBenefit = monthlySpending * (benefitValue.doubleValue() / 100);
                double actualBenefit = Math.min(maxPossibleBenefit, calculatedBenefit);
                return (actualBenefit / monthlySpending) * 100;
            }
            return benefitValue.doubleValue();
        } else {
            // 고정 금액 혜택의 경우 혜택률 계산
            return (benefitValue.doubleValue() / monthlySpending) * 100;
        }
    }
    
    /**
     * 혜택이 퍼센트 혜택인지 판단합니다.
     */
    private static boolean isPercentageBenefit(BigDecimal benefitValue, String conditionText, String title) {
        if ((conditionText != null && conditionText.contains("%")) || 
            (title != null && title.contains("%"))) {
            return true;
        }
        
        if (conditionText != null && conditionText.contains("원")) {
            return false;
        }
        
        return benefitValue.compareTo(BigDecimal.valueOf(10)) <= 0;
    }
    
    /**
     * 사용 조건 적합성 점수를 계산합니다.
     */
    private static double calculateConditionCompatibilityScore(UserProfile userProfile, CardProductVO cardInfo) {
        double score = 100.0; // 기본 점수
        
        // 전월 실적 조건 확인
        Long preMonthMoney = cardInfo.getPreMonthMoney();
        if (preMonthMoney != null && preMonthMoney > 0) {
            double userMonthlySpending = userProfile.getTotalMonthlySpending();
            
            if (userMonthlySpending >= preMonthMoney) {
                score += 0; // 조건 충족 시 추가 점수 없음
            } else {
                // 조건 미충족 시 감점 (부족 비율에 따라)
                double shortfallRatio = (preMonthMoney - userMonthlySpending) / preMonthMoney;
                score -= shortfallRatio * 50; // 최대 50점 감점
            }
        }
        
        // 연회비 대비 혜택 비율 확인
        String annualFee = cardInfo.getAnnualFee();
        if (annualFee != null && !annualFee.equals("무료")) {
            double feeAmount = parseAnnualFee(annualFee);
            double yearlySpending = userProfile.getTotalMonthlySpending() * 12;
            
            if (feeAmount > 0 && yearlySpending > 0) {
                double feeRatio = feeAmount / yearlySpending;
                if (feeRatio > 0.02) { // 연간 사용액의 2% 초과 시 감점
                    score -= (feeRatio - 0.02) * 1000; // 비율에 따라 감점
                }
            }
        }
        
        return Math.max(0.0, Math.min(100.0, score));
    }
    
    /**
     * 라이프스타일 적합성 점수를 계산합니다.
     */
    private static double calculateLifestyleCompatibilityScore(UserProfile userProfile, List<CardParsedBenefitVO> cardBenefits) {
        String lifestyleType = userProfile.getLifestyleType();
        
        if (lifestyleType == null) return 50.0; // 기본 점수
        
        // 라이프스타일별 선호 카테고리 정의
        Map<String, List<String>> lifestylePreferences = new HashMap<>();
        lifestylePreferences.put("프리미엄_여행족", Arrays.asList("여행/숙박", "항공마일리지", "공항라운지/PP", "해외"));
        lifestylePreferences.put("프리미엄_쇼핑족", Arrays.asList("쇼핑", "백화점", "온라인쇼핑", "해외직구"));
        lifestylePreferences.put("외식_선호형", Arrays.asList("푸드", "카페/디저트", "배달앱"));
        lifestylePreferences.put("이동_활발형", Arrays.asList("교통", "주유", "자동차/하이패스"));
        lifestylePreferences.put("문화_생활형", Arrays.asList("OTT/영화/문화", "도서", "공연/전시"));
        lifestylePreferences.put("절약_실용형", Arrays.asList("마트/편의점", "공과금/렌탈", "통신"));
        
        List<String> preferredCategories = lifestylePreferences.getOrDefault(lifestyleType, Collections.emptyList());
        
        if (preferredCategories.isEmpty()) return 50.0;
        
        // 선호 카테고리와 카드 혜택 매칭 확인
        long matchedBenefits = cardBenefits.stream()
            .filter(b -> !"유의사항".equals(b.getTitle()))
            .filter(b -> preferredCategories.stream()
                .anyMatch(cat -> CategoryMappingUtil.isCategoryMatch(b.getCategory(), cat)))
            .count();
        
        // 매칭 비율에 따른 점수 (최대 100점)
        double matchRatio = (double) matchedBenefits / preferredCategories.size();
        return Math.min(100.0, matchRatio * 100 + 20); // 기본 20점 + 매칭 점수
    }
    
    /**
     * 연회비 문자열을 숫자로 파싱합니다.
     */
    private static double parseAnnualFee(String annualFee) {
        if (annualFee == null || "무료".equals(annualFee)) {
            return 0.0;
        }
        
        try {
            String feeNumeric = annualFee.replaceAll("[^0-9]", "");
            return feeNumeric.isEmpty() ? 0.0 : Double.parseDouble(feeNumeric);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * 추천 이유를 생성합니다.
     */
    public static List<String> generateRecommendationReasons(
            UserProfile userProfile,
            List<CardParsedBenefitVO> cardBenefits,
            double matchScore,
            BigDecimal expectedBenefit) {
        
        List<String> reasons = new ArrayList<>();
        
        // 주요 카테고리 매칭 이유
        List<String> primaryCategories = userProfile.getPrimaryCategories();
        Map<String, Double> preferences = userProfile.getCategoryPreferences();
        
        for (String category : primaryCategories) {
            boolean hasMatchingBenefit = cardBenefits.stream()
                .filter(b -> !"유의사항".equals(b.getTitle()))
                .anyMatch(b -> CategoryMappingUtil.isCategoryMatch(b.getCategory(), category));
            
            if (hasMatchingBenefit) {
                double preference = preferences.getOrDefault(category, 0.0);
                reasons.add(String.format("주로 사용하시는 %s 카테고리에서 %.1f%% 비중으로 혜택을 받을 수 있습니다", 
                    category, preference * 100));
            }
        }
        
        // 예상 혜택 금액
        if (expectedBenefit.compareTo(BigDecimal.ZERO) > 0) {
            reasons.add(String.format("월 예상 혜택: %,d원을 받을 수 있습니다", expectedBenefit.intValue()));
        }
        
        // 매칭 점수별 추가 메시지
        if (matchScore >= 80) {
            reasons.add("귀하의 소비 패턴과 매우 잘 맞는 카드입니다");
        } else if (matchScore >= 60) {
            reasons.add("귀하의 소비 패턴과 적합한 카드입니다");
        }
        
        // 라이프스타일 적합성
        String lifestyleType = userProfile.getLifestyleType();
        if (lifestyleType != null) {
            reasons.add(String.format("%s 라이프스타일에 적합한 혜택을 제공합니다", 
                lifestyleType.replace("_", " ")));
        }
        
        return reasons.size() > 3 ? reasons.subList(0, 3) : reasons;
    }
}