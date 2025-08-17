package team2.pjt12.matchumoney.domain.cardrecommendation.util;

import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardParsedBenefitVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardTransactionSummaryVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 고도화된 카드 혜택 계산 유틸리티
 * 사용자 거래 패턴을 분석하여 개인 맞춤형 혜택을 계산합니다.
 */
public class SmartBenefitCalculationUtil {
    
    /**
     * 사용자의 거래 패턴을 기반으로 개인화된 카드 혜택을 계산합니다.
     * 카테고리별 가중치, 거래 빈도, 시간대별 패턴 등을 고려합니다.
     */
    public static BigDecimal calculatePersonalizedBenefit(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long previousMonthSpend,
            Long cardPreMonthMoney,
            Map<String, Double> categoryWeights) {
        
        if (benefits == null || benefits.isEmpty() || 
            transactionSummaries == null || transactionSummaries.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // 카드 전체 전월 실적 조건 확인
        if (cardPreMonthMoney != null && cardPreMonthMoney > 0 && 
            previousMonthSpend < cardPreMonthMoney) {
            return calculateMinimumBenefit(benefits, transactionSummaries, categoryWeights);
        }
        
        BigDecimal totalBenefit = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryBenefits = new HashMap<>();
        
        // 각 거래 카테고리별로 최적 혜택 계산
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            BigDecimal categoryBenefit = calculateOptimalCategoryBenefit(
                benefits, summary, previousMonthSpend, categoryWeights);
            
            if (categoryBenefit.compareTo(BigDecimal.ZERO) > 0) {
                categoryBenefits.put(summary.getCategory(), categoryBenefit);
                totalBenefit = totalBenefit.add(categoryBenefit);
            }
        }
        
        // 카드 전체 월 한도 적용
        Integer cardTotalLimit = findCardTotalMonthlyLimit(benefits);
        if (cardTotalLimit != null && cardTotalLimit > 0) {
            BigDecimal cardLimit = BigDecimal.valueOf(cardTotalLimit);
            if (totalBenefit.compareTo(cardLimit) > 0) {
                totalBenefit = cardLimit;
            }
        }
        
        return totalBenefit.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 특정 카테고리에 대한 최적 혜택을 계산합니다.
     * 개인화 가중치를 적용하여 더 정확한 혜택을 계산합니다.
     */
    private static BigDecimal calculateOptimalCategoryBenefit(
            List<CardParsedBenefitVO> benefits,
            CardTransactionSummaryVO summary,
            Long previousMonthSpend,
            Map<String, Double> categoryWeights) {
        
        BigDecimal maxBenefit = BigDecimal.ZERO;
        String transactionCategory = summary.getCategory();
        
        // 카테고리 가중치 적용 (기본값 1.0)
        Double categoryWeight = categoryWeights.getOrDefault(transactionCategory, 1.0);
        
        for (CardParsedBenefitVO benefit : benefits) {
            if ("유의사항".equals(benefit.getTitle())) {
                continue;
            }
            
            if (CategoryMappingUtil.isCategoryMatch(benefit.getCategory(), transactionCategory)) {
                BigDecimal benefitAmount = calculateEnhancedBenefitAmount(
                    benefit, summary, previousMonthSpend, categoryWeight);
                
                if (benefitAmount.compareTo(maxBenefit) > 0) {
                    maxBenefit = benefitAmount;
                }
            }
        }
        
        return maxBenefit;
    }
    
    /**
     * 개선된 혜택 금액 계산 (개인화 가중치 적용)
     */
    private static BigDecimal calculateEnhancedBenefitAmount(
            CardParsedBenefitVO benefit,
            CardTransactionSummaryVO summary,
            Long previousMonthSpend,
            Double categoryWeight) {
        
        // 기본 혜택 계산
        BigDecimal baseBenefit = BenefitCalculationUtil.calculateBenefitAmount(
            benefit, summary, previousMonthSpend);
        
        if (baseBenefit.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 카테고리 가중치 적용
        BigDecimal weightedBenefit = baseBenefit.multiply(BigDecimal.valueOf(categoryWeight));
        
        // 거래 빈도 보너스 적용
        BigDecimal frequencyBonus = calculateFrequencyBonus(summary, benefit);
        weightedBenefit = weightedBenefit.add(frequencyBonus);
        
        // 월 한도 적용
        Integer maxBenefitMonthly = benefit.getMaxBenefitMonthly();
        if (maxBenefitMonthly != null && maxBenefitMonthly > 0) {
            BigDecimal maxBenefit = BigDecimal.valueOf(maxBenefitMonthly);
            if (weightedBenefit.compareTo(maxBenefit) > 0) {
                weightedBenefit = maxBenefit;
            }
        }
        
        return weightedBenefit.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 거래 빈도에 따른 보너스 혜택을 계산합니다.
     */
    private static BigDecimal calculateFrequencyBonus(
            CardTransactionSummaryVO summary,
            CardParsedBenefitVO benefit) {
        
        Integer transactionCount = summary.getTransactionCount();
        if (transactionCount == null || transactionCount <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 거래 건수가 많을수록 보너스 혜택 증가 (최대 10% 추가)
        double frequencyMultiplier = Math.min(0.10, transactionCount * 0.002);
        
        BigDecimal benefitValue = benefit.getValue();
        if (benefitValue == null) {
            return BigDecimal.ZERO;
        }
        
        return benefitValue.multiply(BigDecimal.valueOf(frequencyMultiplier));
    }
    
    /**
     * 사용자의 거래 패턴을 분석하여 카테고리별 가중치를 계산합니다.
     */
    public static Map<String, Double> calculateCategoryWeights(
            List<CardTransactionSummaryVO> transactionSummaries) {
        
        Map<String, Double> weights = new HashMap<>();
        
        if (transactionSummaries == null || transactionSummaries.isEmpty()) {
            return weights;
        }
        
        // 총 거래 금액 계산
        Long totalAmount = transactionSummaries.stream()
            .mapToLong(CardTransactionSummaryVO::getTotalAmount)
            .sum();
        
        // 총 거래 건수 계산
        Integer totalTransactions = transactionSummaries.stream()
            .mapToInt(s -> s.getTransactionCount() != null ? s.getTransactionCount() : 0)
            .sum();
        
        if (totalAmount <= 0 || totalTransactions <= 0) {
            return weights;
        }
        
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            String category = summary.getCategory();
            
            // 금액 비중 (0.6 가중치)
            double amountRatio = (double) summary.getTotalAmount() / totalAmount;
            
            // 거래 빈도 비중 (0.4 가중치)
            double frequencyRatio = summary.getTransactionCount() != null ? 
                (double) summary.getTransactionCount() / totalTransactions : 0.0;
            
            // 가중 평균으로 최종 가중치 계산
            double categoryWeight = (amountRatio * 0.6) + (frequencyRatio * 0.4);
            
            // 최소 0.5, 최대 2.0 범위로 정규화
            categoryWeight = Math.max(0.5, Math.min(2.0, categoryWeight * 5));
            
            weights.put(category, categoryWeight);
        }
        
        return weights;
    }
    
    /**
     * 전월 실적 미달 시 최소 혜택을 계산합니다.
     */
    private static BigDecimal calculateMinimumBenefit(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Map<String, Double> categoryWeights) {
        
        BigDecimal totalBenefit = BigDecimal.ZERO;
        
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            BigDecimal categoryBenefit = BigDecimal.ZERO;
            
            for (CardParsedBenefitVO benefit : benefits) {
                if ("유의사항".equals(benefit.getTitle())) {
                    continue;
                }
                
                // 전월 실적 조건이 없는 혜택만 계산
                if (benefit.getPreMonthMoneySpecific() == null || 
                    benefit.getPreMonthMoneySpecific() <= 0) {
                    
                    if (CategoryMappingUtil.isCategoryMatch(benefit.getCategory(), summary.getCategory())) {
                        BigDecimal benefitAmount = BenefitCalculationUtil.calculateBenefitAmount(
                            benefit, summary, 0L);
                        
                        // 카테고리 가중치 적용
                        Double weight = categoryWeights.getOrDefault(summary.getCategory(), 1.0);
                        benefitAmount = benefitAmount.multiply(BigDecimal.valueOf(weight));
                        
                        if (benefitAmount.compareTo(categoryBenefit) > 0) {
                            categoryBenefit = benefitAmount;
                        }
                    }
                }
            }
            
            totalBenefit = totalBenefit.add(categoryBenefit);
        }
        
        return totalBenefit.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 카테고리별 상세 혜택 분석을 제공합니다.
     */
    public static Map<String, Map<String, Object>> analyzeCategoryBenefits(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long previousMonthSpend,
            Map<String, Double> categoryWeights) {
        
        Map<String, Map<String, Object>> analysis = new HashMap<>();
        
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            Map<String, Object> categoryAnalysis = new HashMap<>();
            String category = summary.getCategory();
            
            // 해당 카테고리의 최적 혜택 찾기
            List<CardParsedBenefitVO> applicableBenefits = benefits.stream()
                .filter(b -> !"유의사항".equals(b.getTitle()))
                .filter(b -> CategoryMappingUtil.isCategoryMatch(b.getCategory(), category))
                .collect(Collectors.toList());
            
            if (!applicableBenefits.isEmpty()) {
                // 예상 혜택 계산
                BigDecimal expectedBenefit = calculateOptimalCategoryBenefit(
                    benefits, summary, previousMonthSpend, categoryWeights);
                
                // 혜택률 계산
                double benefitRate = summary.getTotalAmount() > 0 ? 
                    expectedBenefit.doubleValue() / summary.getTotalAmount() * 100 : 0.0;
                
                categoryAnalysis.put("expectedBenefit", expectedBenefit);
                categoryAnalysis.put("benefitRate", benefitRate);
                categoryAnalysis.put("transactionAmount", summary.getTotalAmount());
                categoryAnalysis.put("transactionCount", summary.getTransactionCount());
                categoryAnalysis.put("categoryWeight", categoryWeights.getOrDefault(category, 1.0));
                categoryAnalysis.put("applicableBenefitsCount", applicableBenefits.size());
                
                analysis.put(category, categoryAnalysis);
            }
        }
        
        return analysis;
    }
    
    /**
     * 예상 월 혜택과 연 혜택을 계산합니다.
     */
    public static Map<String, BigDecimal> calculateProjectedBenefits(
            BigDecimal currentMonthBenefit,
            List<CardTransactionSummaryVO> transactionSummaries) {
        
        Map<String, BigDecimal> projectedBenefits = new HashMap<>();
        
        // 월 혜택 (현재 계산된 값)
        projectedBenefits.put("monthly", currentMonthBenefit);
        
        // 연 혜택 추정 (월 혜택 × 12, 계절성 고려)
        BigDecimal yearlyBenefit = currentMonthBenefit.multiply(BigDecimal.valueOf(12));
        
        // 계절성 조정 인수 적용 (특정 카테고리의 계절적 변동 고려)
        double seasonalityFactor = calculateSeasonalityFactor(transactionSummaries);
        yearlyBenefit = yearlyBenefit.multiply(BigDecimal.valueOf(seasonalityFactor));
        
        projectedBenefits.put("yearly", yearlyBenefit.setScale(0, RoundingMode.HALF_UP));
        
        return projectedBenefits;
    }
    
    /**
     * 거래 패턴을 기반으로 계절성 인수를 계산합니다.
     */
    private static double calculateSeasonalityFactor(List<CardTransactionSummaryVO> transactionSummaries) {
        // 기본값: 1.0 (변동 없음)
        double seasonalityFactor = 1.0;
        
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            String category = summary.getCategory();
            BigDecimal ratio = summary.getCategoryRatio();
            
            if (ratio == null) continue;
            
            // 카테고리별 계절성 영향도
            double categorySeasonality = getCategorySeasonality(category);
            double categoryWeight = ratio.doubleValue();
            
            // 가중 평균으로 전체 계절성 인수 계산
            seasonalityFactor += (categorySeasonality - 1.0) * categoryWeight;
        }
        
        // 0.8 ~ 1.2 범위로 제한
        return Math.max(0.8, Math.min(1.2, seasonalityFactor));
    }
    
    /**
     * 카테고리별 계절성 인수를 반환합니다.
     */
    private static double getCategorySeasonality(String category) {
        switch (category) {
            case "여행/숙박":
                return 1.15; // 여행은 계절적 변동이 큼
            case "쇼핑":
                return 1.10; // 쇼핑도 계절적 영향 있음
            case "레저/스포츠":
                return 1.08; // 레저도 계절 영향
            case "교통":
            case "푸드":
            case "마트/편의점":
                return 0.98; // 필수 소비는 비교적 안정적
            case "통신":
            case "공과금/렌탈":
                return 0.95; // 고정비는 매우 안정적
            default:
                return 1.0; // 기타는 변동 없음
        }
    }
    
    /**
     * 카드 전체 월 한도를 찾습니다.
     */
    private static Integer findCardTotalMonthlyLimit(List<CardParsedBenefitVO> benefits) {
        return benefits.stream()
            .filter(b -> "유의사항".equals(b.getTitle()))
            .filter(b -> b.getMaxBenefitMonthly() != null && b.getMaxBenefitMonthly() > 0)
            .findFirst()
            .map(CardParsedBenefitVO::getMaxBenefitMonthly)
            .orElse(null);
    }
    
    /**
     * 혜택 효율성 점수를 계산합니다 (0-100점)
     */
    public static double calculateBenefitEfficiencyScore(
            BigDecimal expectedBenefit,
            Long totalSpending,
            String annualFee) {
        
        if (totalSpending == null || totalSpending <= 0) {
            return 0.0;
        }
        
        // 혜택률 계산 (혜택/사용액 * 100)
        double benefitRate = expectedBenefit.doubleValue() / totalSpending * 100;
        
        // 연회비 고려
        double annualFeeAmount = parseAnnualFee(annualFee);
        double netBenefitRate = (expectedBenefit.doubleValue() * 12 - annualFeeAmount) / (totalSpending * 12) * 100;
        
        // 점수 계산 (최대 100점)
        double score = Math.min(100, netBenefitRate * 20); // 5% 혜택률이면 100점
        
        return Math.max(0, score);
    }
    
    /**
     * 연회비 문자열을 숫자로 변환합니다.
     */
    private static double parseAnnualFee(String annualFee) {
        if (annualFee == null || "무료".equals(annualFee) || annualFee.trim().isEmpty()) {
            return 0.0;
        }
        
        try {
            String feeNumeric = annualFee.replaceAll("[^0-9]", "");
            return feeNumeric.isEmpty() ? 0.0 : Double.parseDouble(feeNumeric);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}