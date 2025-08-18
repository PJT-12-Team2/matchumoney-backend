package team2.pjt12.matchumoney.domain.cardrecommendation.util;

import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardParsedBenefitVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardTransactionSummaryVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.CategoryMappingUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
                // 고정 금액 혜택 계산 - 더 안전한 로직
                if (conditionText != null && conditionText.contains("건당")) {
                    // 건당 고정 혜택: 혜택금액 × 거래건수 (단, 합리적 범위 내에서)
                    Integer transactionCount = transactionSummary.getTransactionCount();
                    if (transactionCount != null && transactionCount > 0) {
                        // 건당 혜택이 과도하게 높지 않도록 제한 (건당 최대 5,000원)
                        BigDecimal maxPerTransaction = BigDecimal.valueOf(5000);
                        BigDecimal actualBenefitPerTransaction = benefitValue.min(maxPerTransaction);
                        
                        benefitAmount = actualBenefitPerTransaction.multiply(BigDecimal.valueOf(transactionCount));
                        
                        // 총 혜택이 사용액을 초과하지 않도록 제한
                        if (benefitAmount.compareTo(transactionAmount) > 0) {
                            benefitAmount = transactionAmount.multiply(BigDecimal.valueOf(0.5)); // 최대 50% 혜택
                        }
                    } else {
                        benefitAmount = benefitValue;
                    }
                } else if (conditionText != null && (conditionText.contains("월") || conditionText.contains("매월"))) {
                    // 월 단위 고정 혜택: 조건 만족 시 한 번만 지급
                    benefitAmount = benefitValue;
                } else {
                    // 사용액 기준 고정 혜택: 일정 사용액당 혜택
                    if (conditionText != null && conditionText.contains("만원당")) {
                        String[] parts = conditionText.split("만원당");
                        if (parts.length > 0) {
                            try {
                                int baseAmount = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                                if (baseAmount > 0) {
                                    int timesEligible = (int) (transactionAmount.longValue() / (baseAmount * 10000));
                                    benefitAmount = benefitValue.multiply(BigDecimal.valueOf(timesEligible));
                                } else {
                                    benefitAmount = BigDecimal.ZERO; // 조건 미충족
                                }
                            } catch (NumberFormatException e) {
                                benefitAmount = BigDecimal.ZERO;
                            }
                        } else {
                            benefitAmount = BigDecimal.ZERO;
                        }
                    } else {
                        // 기본 고정 혜택 - 월 한도가 있는 경우만 적용
                        Integer maxBenefit = benefit.getMaxBenefitMonthly();
                        if (maxBenefit != null && maxBenefit > 0) {
                            benefitAmount = benefitValue;
                        } else {
                            // 월 한도가 없는 무제한 고정 혜택은 의심스러우므로 제한
                            benefitAmount = BigDecimal.ZERO;
                        }
                    }
                }
            }
        }
        
        // 개별 혜택의 월 최대 한도 적용
        // max_benefit_monthly가 null, 0이면 한도 없음, 양수면 해당 값으로 한도 적용
        Integer maxBenefitMonthly = benefit.getMaxBenefitMonthly();
        if (maxBenefitMonthly != null && maxBenefitMonthly > 0) {
            BigDecimal maxBenefit = BigDecimal.valueOf(maxBenefitMonthly);
            if (benefitAmount.compareTo(maxBenefit) > 0) {
                benefitAmount = maxBenefit;
            }
        }
        // maxBenefitMonthly가 0이거나 null이면 한도 제한 없음
        
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
        // 1. 명시적으로 '%' 표시가 있으면 퍼센트 혜택
        if ((conditionText != null && conditionText.contains("%")) || 
            (title != null && title.contains("%"))) {
            return true;
        }
        
        // 2. 명시적으로 '원' 표시가 있으면 고정 금액 혜택
        if (conditionText != null && (conditionText.contains("원") || conditionText.contains("포인트"))) {
            return false;
        }
        
        // 3. 건당, 월단위 등의 키워드가 있으면 고정 금액 혜택
        if (conditionText != null && (conditionText.contains("건당") || 
            conditionText.contains("월") || conditionText.contains("매월") ||
            conditionText.contains("만원당"))) {
            return false;
        }
        
        // 4. 혜택 값이 1000 이상이면 고정 금액일 가능성 높음
        if (benefitValue.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return false;
        }
        
        // 5. 혜택 값이 50 이하이고 정수가 아니면 퍼센트일 가능성 높음
        if (benefitValue.compareTo(BigDecimal.valueOf(50)) <= 0 && 
            benefitValue.scale() > 0) {
            return true;
        }
        
        // 6. 혜택 값이 10 이하이면 퍼센트로 추정 (일반적인 카드 혜택율)
        if (benefitValue.compareTo(BigDecimal.valueOf(10)) <= 0) {
            return true;
        }
        
        // 7. 혜택 값이 10-100 사이면 조건부로 판단
        if (benefitValue.compareTo(BigDecimal.valueOf(100)) <= 0) {
            // 소수점이 있거나, 50 이하면 퍼센트로 추정
            if (benefitValue.scale() > 0 || benefitValue.compareTo(BigDecimal.valueOf(50)) <= 0) {
                return true;
            }
        }
        
        // 8. 그 외는 고정 금액으로 가정
        return false;
    }
    
    /**
     * 카드의 모든 혜택을 기반으로 총 혜택 금액을 계산합니다.
     * 월 최대 혜택 한도를 정확히 적용합니다.
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
        
        // title이 "유의사항"인 혜택에서 카드 전체 월 한도 찾기
        Integer cardTotalMonthlyLimit = findCardTotalMonthlyLimit(benefits);
        
        return calculateTotalBenefit(benefits, transactionSummaries, previousMonthSpend, cardPreMonthMoney, cardTotalMonthlyLimit);
    }
    
    /**
     * 카드의 모든 혜택을 기반으로 총 혜택 금액을 계산합니다.
     * 개별 혜택 한도와 카드 전체 월 한도를 모두 적용합니다.
     * @param benefits 카드의 모든 혜택 목록
     * @param transactionSummaries 카테고리별 거래 요약 목록
     * @param previousMonthSpend 전월 총 사용액
     * @param cardPreMonthMoney 카드 전체 전월 실적 조건
     * @param cardTotalMonthlyLimit 카드 전체 월 한도 (null이면 제한 없음)
     * @return 총 혜택 금액
     */
    public static BigDecimal calculateTotalBenefit(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long previousMonthSpend,
            Long cardPreMonthMoney,
            Integer cardTotalMonthlyLimit) {
        
        // 카드 전체 전월 실적 조건 확인
        if (cardPreMonthMoney != null && cardPreMonthMoney > 0 && previousMonthSpend < cardPreMonthMoney) {
            // 전월 실적 미달 시에도 최소 혜택은 계산 (실적 조건 없는 혜택들)
            return calculateBenefitWithoutPerformanceRequirement(benefits, transactionSummaries, previousMonthSpend, cardTotalMonthlyLimit);
        }
        
        return calculateTotalBenefitWithCategoryLimits(benefits, transactionSummaries, previousMonthSpend, cardTotalMonthlyLimit);
    }
    
    /**
     * 카테고리별 혜택 한도를 고려하여 총 혜택을 계산합니다.
     * @param benefits 카드의 모든 혜택 목록
     * @param transactionSummaries 카테고리별 거래 요약 목록
     * @param previousMonthSpend 전월 총 사용액
     * @param cardTotalMonthlyLimit 카드 전체 월 한도 (null이면 제한 없음)
     * @return 총 혜택 금액
     */
    private static BigDecimal calculateTotalBenefitWithCategoryLimits(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long previousMonthSpend,
            Integer cardTotalMonthlyLimit) {
        
        BigDecimal totalBenefit = BigDecimal.ZERO;
        
        // 모든가맹점 혜택 및 선택형 혜택 확인
        CardParsedBenefitVO allMerchantsBenefit = findAllMerchantsBenefit(benefits);
        CardParsedBenefitVO selectiveBenefit = findSelectiveBenefit(benefits);
        
        // 각 거래 카테고리별로 최적의 혜택 계산
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            BigDecimal categoryBenefit = calculateBestBenefitForCategory(
                benefits, summary, previousMonthSpend);
            
            // 모든가맹점 혜택이 있는 경우 추가 계산
            if (allMerchantsBenefit != null) {
                BigDecimal allMerchantsBenefitAmount = calculateAllMerchantsBenefit(
                    allMerchantsBenefit, summary, previousMonthSpend);
                
                // 카테고리별 혜택과 모든가맹점 혜택 중 더 큰 값 선택
                if (allMerchantsBenefitAmount.compareTo(categoryBenefit) > 0) {
                    categoryBenefit = allMerchantsBenefitAmount;
                }
            }
            
            // 선택형 혜택이 있는 경우 추가 계산
            if (selectiveBenefit != null) {
                BigDecimal selectiveBenefitAmount = calculateSelectiveBenefit(
                    selectiveBenefit, summary, previousMonthSpend);
                
                // 기존 혜택과 선택형 혜택 중 더 큰 값 선택
                if (selectiveBenefitAmount.compareTo(categoryBenefit) > 0) {
                    categoryBenefit = selectiveBenefitAmount;
                }
            }
            
            totalBenefit = totalBenefit.add(categoryBenefit);
        }
        
        // 카드 전체 월 한도 적용
        if (cardTotalMonthlyLimit != null && cardTotalMonthlyLimit > 0) {
            BigDecimal cardLimit = BigDecimal.valueOf(cardTotalMonthlyLimit);
            if (totalBenefit.compareTo(cardLimit) > 0) {
                totalBenefit = cardLimit;
            }
        }
        
        // 전체 사용액 대비 혜택이 과도하지 않도록 추가 안전장치
        if (previousMonthSpend != null && previousMonthSpend > 0) {
            BigDecimal totalSpendAmount = BigDecimal.valueOf(previousMonthSpend);
            BigDecimal maxReasonableBenefit = totalSpendAmount.multiply(BigDecimal.valueOf(0.20)); // 최대 20% 혜택
            
            if (totalBenefit.compareTo(maxReasonableBenefit) > 0) {
                totalBenefit = maxReasonableBenefit;
            }
        }
        
        return totalBenefit.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 특정 거래 카테고리에 대한 최적의 혜택을 계산합니다.
     * 여러 혜택이 해당 카테고리에 적용될 수 있을 때, 월 한도를 고려하여 최고 혜택을 선택합니다.
     * @param benefits 모든 혜택 목록
     * @param transactionSummary 거래 요약
     * @param previousMonthSpend 전월 사용액
     * @return 해당 카테고리의 최적 혜택 금액
     */
    private static BigDecimal calculateBestBenefitForCategory(
            List<CardParsedBenefitVO> benefits,
            CardTransactionSummaryVO transactionSummary,
            Long previousMonthSpend) {
        
        BigDecimal maxBenefit = BigDecimal.ZERO;
        String transactionCategory = transactionSummary.getCategory();
        
        // 해당 거래 카테고리에 적용 가능한 모든 혜택 중 최고 혜택 선택 (유의사항 제외)
        for (CardParsedBenefitVO benefit : benefits) {
            // 유의사항은 혜택 계산에서 제외
            if ("유의사항".equals(benefit.getTitle())) {
                continue;
            }
            
            // 카테고리 매칭 확인
            if (isCategoryMatched(benefit.getCategory(), transactionCategory)) {
                BigDecimal benefitAmount = calculateBenefitAmount(benefit, transactionSummary, previousMonthSpend);
                
                // 현재까지의 최고 혜택과 비교
                if (benefitAmount.compareTo(maxBenefit) > 0) {
                    maxBenefit = benefitAmount;
                }
            }
        }
        
        return maxBenefit;
    }
    
    /**
     * 전월 실적 조건 없는 혜택들만 계산합니다.
     * @param benefits 모든 혜택 목록
     * @param transactionSummaries 거래 요약 목록
     * @param previousMonthSpend 전월 사용액
     * @param cardTotalMonthlyLimit 카드 전체 월 한도 (null이면 제한 없음)
     * @return 실적 조건 없는 혜택 합계
     */
    private static BigDecimal calculateBenefitWithoutPerformanceRequirement(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long previousMonthSpend,
            Integer cardTotalMonthlyLimit) {
        
        BigDecimal totalBenefit = BigDecimal.ZERO;
        
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            BigDecimal categoryBenefit = BigDecimal.ZERO;
            
            for (CardParsedBenefitVO benefit : benefits) {
                // 유의사항은 혜택 계산에서 제외
                if ("유의사항".equals(benefit.getTitle())) {
                    continue;
                }
                
                // 개별 혜택의 전월 실적 조건이 없는 경우만 계산
                if (benefit.getPreMonthMoneySpecific() == null || benefit.getPreMonthMoneySpecific() <= 0) {
                    if (isCategoryMatched(benefit.getCategory(), summary.getCategory())) {
                        BigDecimal benefitAmount = calculateBenefitAmount(benefit, summary, previousMonthSpend);
                        if (benefitAmount.compareTo(categoryBenefit) > 0) {
                            categoryBenefit = benefitAmount;
                        }
                    }
                }
            }
            
            totalBenefit = totalBenefit.add(categoryBenefit);
        }
        
        // 카드 전체 월 한도 적용
        if (cardTotalMonthlyLimit != null && cardTotalMonthlyLimit > 0) {
            BigDecimal cardLimit = BigDecimal.valueOf(cardTotalMonthlyLimit);
            if (totalBenefit.compareTo(cardLimit) > 0) {
                totalBenefit = cardLimit;
            }
        }
        // cardTotalMonthlyLimit이 null이거나 0이면 제한 없음
        
        return totalBenefit.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 카테고리별 혜택 금액을 상세하게 계산합니다.
     * 각 카테고리별로 최적의 혜택을 선택하고 월 한도를 적용합니다.
     * @param benefits 모든 혜택 목록
     * @param transactionSummaries 거래 요약 목록
     * @param previousMonthSpend 전월 사용액
     * @param cardPreMonthMoney 카드 전체 전월 실적 조건
     * @return 카테고리별 혜택 금액 맵
     */
    public static Map<String, BigDecimal> calculateCategoryBenefits(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long previousMonthSpend,
            Long cardPreMonthMoney) {
        
        // title이 "유의사항"인 혜택에서 카드 전체 월 한도 찾기
        Integer cardTotalMonthlyLimit = findCardTotalMonthlyLimit(benefits);
        
        return calculateCategoryBenefits(benefits, transactionSummaries, previousMonthSpend, cardPreMonthMoney, cardTotalMonthlyLimit);
    }
    
    /**
     * 카테고리별 혜택 금액을 상세하게 계산합니다.
     * 각 카테고리별로 최적의 혜택을 선택하고 개별 및 전체 월 한도를 적용합니다.
     * @param benefits 모든 혜택 목록
     * @param transactionSummaries 거래 요약 목록
     * @param previousMonthSpend 전월 사용액
     * @param cardPreMonthMoney 카드 전체 전월 실적 조건
     * @param cardTotalMonthlyLimit 카드 전체 월 한도 (null이면 제한 없음)
     * @return 카테고리별 혜택 금액 맵
     */
    public static Map<String, BigDecimal> calculateCategoryBenefits(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long previousMonthSpend,
            Long cardPreMonthMoney,
            Integer cardTotalMonthlyLimit) {
        
        Map<String, BigDecimal> categoryBenefits = new HashMap<>();
        
        // 카드 전체 전월 실적 조건 확인
        boolean meetsCardRequirement = cardPreMonthMoney == null || 
                                      cardPreMonthMoney <= 0 || 
                                      previousMonthSpend >= cardPreMonthMoney;
        
        BigDecimal totalCalculatedBenefit = BigDecimal.ZERO;
        
        // 모든가맹점 혜택 및 선택형 혜택 확인
        CardParsedBenefitVO allMerchantsBenefit = findAllMerchantsBenefit(benefits);
        CardParsedBenefitVO selectiveBenefit = findSelectiveBenefit(benefits);
        
        // 1단계: 각 카테고리별 최적 혜택 계산 (유의사항 제외)
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            BigDecimal categoryBenefit = BigDecimal.ZERO;
            String transactionCategory = summary.getCategory();
            
            // 카테고리 특화 혜택 계산
            for (CardParsedBenefitVO benefit : benefits) {
                // 유의사항은 혜택 계산에서 제외 (월 한도 정보로만 사용)
                if ("유의사항".equals(benefit.getTitle())) {
                    continue;
                }
                
                if (isCategoryMatched(benefit.getCategory(), transactionCategory)) {
                    // 개별 혜택의 전월 실적 조건 확인
                    boolean meetsBenefitRequirement = benefit.getPreMonthMoneySpecific() == null ||
                                                     benefit.getPreMonthMoneySpecific() <= 0 ||
                                                     previousMonthSpend >= benefit.getPreMonthMoneySpecific();
                    
                    // 카드 전체 조건과 개별 혜택 조건을 모두 만족해야 함
                    if (meetsCardRequirement && meetsBenefitRequirement) {
                        BigDecimal benefitAmount = calculateBenefitAmount(benefit, summary, previousMonthSpend);
                        if (benefitAmount.compareTo(categoryBenefit) > 0) {
                            categoryBenefit = benefitAmount;
                        }
                    }
                }
            }
            
            // 모든가맹점 혜택과 카테고리 특화 혜택 중 더 큰 값 선택
            if (allMerchantsBenefit != null && meetsCardRequirement) {
                BigDecimal allMerchantsBenefitAmount = calculateAllMerchantsBenefit(
                    allMerchantsBenefit, summary, previousMonthSpend);
                
                if (allMerchantsBenefitAmount.compareTo(categoryBenefit) > 0) {
                    categoryBenefit = allMerchantsBenefitAmount;
                }
            }
            
            // 선택형 혜택과 기존 혜택 중 더 큰 값 선택
            if (selectiveBenefit != null && meetsCardRequirement) {
                BigDecimal selectiveBenefitAmount = calculateSelectiveBenefit(
                    selectiveBenefit, summary, previousMonthSpend);
                
                if (selectiveBenefitAmount.compareTo(categoryBenefit) > 0) {
                    categoryBenefit = selectiveBenefitAmount;
                }
            }
            
            if (categoryBenefit.compareTo(BigDecimal.ZERO) > 0) {
                categoryBenefits.put(transactionCategory, categoryBenefit);
                totalCalculatedBenefit = totalCalculatedBenefit.add(categoryBenefit);
            }
        }
        
        // 2단계: 카드 전체 월 한도 적용 (비례 배분)
        if (cardTotalMonthlyLimit != null && cardTotalMonthlyLimit > 0) {
            BigDecimal cardLimit = BigDecimal.valueOf(cardTotalMonthlyLimit);
            
            if (totalCalculatedBenefit.compareTo(cardLimit) > 0) {
                // 전체 한도를 초과하는 경우, 각 카테고리별로 비례하여 조정
                BigDecimal ratio = cardLimit.divide(totalCalculatedBenefit, 10, RoundingMode.HALF_UP);
                
                Map<String, BigDecimal> adjustedBenefits = new HashMap<>();
                for (Map.Entry<String, BigDecimal> entry : categoryBenefits.entrySet()) {
                    BigDecimal adjustedBenefit = entry.getValue().multiply(ratio).setScale(0, RoundingMode.HALF_UP);
                    if (adjustedBenefit.compareTo(BigDecimal.ZERO) > 0) {
                        adjustedBenefits.put(entry.getKey(), adjustedBenefit);
                    }
                }
                categoryBenefits = adjustedBenefits;
            }
        }
        
        return categoryBenefits;
    }
    
    /**
     * 특정 혜택의 예상 최대 혜택 금액을 계산합니다.
     * 월 한도를 고려하여 실제 받을 수 있는 최대 혜택을 계산합니다.
     * @param benefit 혜택 정보
     * @param monthlySpendingInCategory 해당 카테고리 월 사용액
     * @return 예상 최대 혜택 금액
     */
    public static BigDecimal calculateMaxPossibleBenefit(
            CardParsedBenefitVO benefit, 
            BigDecimal monthlySpendingInCategory) {
        
        if (benefit == null || monthlySpendingInCategory == null || 
            monthlySpendingInCategory.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal benefitValue = benefit.getValue();
        if (benefitValue == null || benefitValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal calculatedBenefit = BigDecimal.ZERO;
        String conditionText = benefit.getConditionText();
        
        // 퍼센트 혜택인지 확인
        boolean isPercentage = isPercentageBenefit(benefitValue, conditionText, benefit.getTitle());
        
        if (isPercentage) {
            // 퍼센트 혜택: 사용액 × 혜택율
            calculatedBenefit = monthlySpendingInCategory
                .multiply(benefitValue)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // 고정 금액 혜택
            calculatedBenefit = benefitValue;
        }
        
        // 월 최대 혜택 한도 적용
        Integer maxBenefitMonthly = benefit.getMaxBenefitMonthly();
        if (maxBenefitMonthly != null && maxBenefitMonthly > 0) {
            BigDecimal maxBenefit = BigDecimal.valueOf(maxBenefitMonthly);
            if (calculatedBenefit.compareTo(maxBenefit) > 0) {
                calculatedBenefit = maxBenefit;
            }
        }
        
        return calculatedBenefit.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 카드 전체 월 한도를 찾습니다.
     * title이 "유의사항"인 혜택의 max_benefit_monthly 값을 카드 전체 월 한도로 사용합니다.
     * @param benefits 카드의 모든 혜택 목록
     * @return 카드 전체 월 한도 (찾지 못하면 null)
     */
    private static Integer findCardTotalMonthlyLimit(List<CardParsedBenefitVO> benefits) {
        if (benefits == null || benefits.isEmpty()) {
            return null;
        }
        
        for (CardParsedBenefitVO benefit : benefits) {
            if ("유의사항".equals(benefit.getTitle()) && 
                benefit.getMaxBenefitMonthly() != null && 
                benefit.getMaxBenefitMonthly() > 0) {
                return benefit.getMaxBenefitMonthly();
            }
        }
        
        return null; // 유의사항에서 월 한도를 찾지 못한 경우
    }
    
    /**
     * 모든가맹점 혜택을 찾습니다.
     * title이 "모든가맹점"이고 category가 "기타"이며 value가 3 미만인 혜택을 찾습니다.
     * @param benefits 카드의 모든 혜택 목록
     * @return 모든가맹점 혜택 (찾지 못하면 null)
     */
    private static CardParsedBenefitVO findAllMerchantsBenefit(List<CardParsedBenefitVO> benefits) {
        if (benefits == null || benefits.isEmpty()) {
            return null;
        }
        
        for (CardParsedBenefitVO benefit : benefits) {
            if ("모든가맹점".equals(benefit.getTitle()) && 
                "기타".equals(benefit.getCategory()) &&
                benefit.getValue() != null &&
                benefit.getValue().compareTo(BigDecimal.valueOf(3)) < 0) {
                return benefit;
            }
        }
        
        return null; // 모든가맹점 혜택을 찾지 못한 경우
    }
    
    /**
     * 선택형 혜택을 찾습니다.
     * title이 "선택형"이고 category가 "기타"이며 benefit_type이 "적립" 또는 "할인"이고 value가 3 미만인 혜택을 찾습니다.
     * @param benefits 카드의 모든 혜택 목록
     * @return 선택형 혜택 (찾지 못하면 null)
     */
    private static CardParsedBenefitVO findSelectiveBenefit(List<CardParsedBenefitVO> benefits) {
        if (benefits == null || benefits.isEmpty()) {
            return null;
        }
        
        for (CardParsedBenefitVO benefit : benefits) {
            if ("선택형".equals(benefit.getTitle()) && 
                "기타".equals(benefit.getCategory()) &&
                ("적립".equals(benefit.getBenefitType()) || "할인".equals(benefit.getBenefitType())) &&
                benefit.getValue() != null &&
                benefit.getValue().compareTo(BigDecimal.valueOf(3)) < 0) {
                return benefit;
            }
        }
        
        return null; // 선택형 혜택을 찾지 못한 경우
    }
    
    /**
     * 선택형 혜택을 계산합니다.
     * 모든 거래에 동일한 퍼센테이지를 적용합니다.
     * @param selectiveBenefit 선택형 혜택 정보
     * @param transactionSummary 거래 요약
     * @param previousMonthSpend 전월 총 사용액
     * @return 선택형 혜택 금액
     */
    private static BigDecimal calculateSelectiveBenefit(
            CardParsedBenefitVO selectiveBenefit,
            CardTransactionSummaryVO transactionSummary,
            Long previousMonthSpend) {
        
        if (selectiveBenefit == null || transactionSummary == null) {
            return BigDecimal.ZERO;
        }
        
        // 개별 혜택의 전월 실적 조건 확인
        Integer requiredPreMonthMoney = selectiveBenefit.getPreMonthMoneySpecific();
        if (requiredPreMonthMoney != null && previousMonthSpend < requiredPreMonthMoney) {
            return BigDecimal.ZERO;
        }
        
        // 건당 최소 결제 금액 조건 확인
        if (selectiveBenefit.getMinSpendPerTransaction() != null) {
            BigDecimal avgAmount = transactionSummary.getAverageAmount();
            if (avgAmount == null || avgAmount.compareTo(BigDecimal.valueOf(selectiveBenefit.getMinSpendPerTransaction())) < 0) {
                return BigDecimal.ZERO;
            }
        }
        
        BigDecimal benefitValue = selectiveBenefit.getValue();
        if (benefitValue == null || benefitValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 거래 총액에 퍼센테이지 적용
        BigDecimal transactionAmount = BigDecimal.valueOf(transactionSummary.getTotalAmount());
        BigDecimal benefitAmount = transactionAmount
            .multiply(benefitValue)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        // 개별 혜택의 월 최대 한도 적용
        Integer maxBenefitMonthly = selectiveBenefit.getMaxBenefitMonthly();
        if (maxBenefitMonthly != null && maxBenefitMonthly > 0) {
            BigDecimal maxBenefit = BigDecimal.valueOf(maxBenefitMonthly);
            if (benefitAmount.compareTo(maxBenefit) > 0) {
                benefitAmount = maxBenefit;
            }
        }
        
        return benefitAmount.setScale(0, RoundingMode.HALF_UP);
    }
    
    /**
     * 모든가맹점 혜택을 계산합니다.
     * 모든 거래에 동일한 퍼센테이지를 적용합니다.
     * @param allMerchantsBenefit 모든가맹점 혜택 정보
     * @param transactionSummary 거래 요약
     * @param previousMonthSpend 전월 총 사용액
     * @return 모든가맹점 혜택 금액
     */
    private static BigDecimal calculateAllMerchantsBenefit(
            CardParsedBenefitVO allMerchantsBenefit,
            CardTransactionSummaryVO transactionSummary,
            Long previousMonthSpend) {
        
        if (allMerchantsBenefit == null || transactionSummary == null) {
            return BigDecimal.ZERO;
        }
        
        // 개별 혜택의 전월 실적 조건 확인
        Integer requiredPreMonthMoney = allMerchantsBenefit.getPreMonthMoneySpecific();
        if (requiredPreMonthMoney != null && previousMonthSpend < requiredPreMonthMoney) {
            return BigDecimal.ZERO;
        }
        
        // 건당 최소 결제 금액 조건 확인
        if (allMerchantsBenefit.getMinSpendPerTransaction() != null) {
            BigDecimal avgAmount = transactionSummary.getAverageAmount();
            if (avgAmount == null || avgAmount.compareTo(BigDecimal.valueOf(allMerchantsBenefit.getMinSpendPerTransaction())) < 0) {
                return BigDecimal.ZERO;
            }
        }
        
        BigDecimal benefitValue = allMerchantsBenefit.getValue();
        if (benefitValue == null || benefitValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // 거래 총액에 퍼센테이지 적용
        BigDecimal transactionAmount = BigDecimal.valueOf(transactionSummary.getTotalAmount());
        BigDecimal benefitAmount = transactionAmount
            .multiply(benefitValue)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        // 개별 혜택의 월 최대 한도 적용
        Integer maxBenefitMonthly = allMerchantsBenefit.getMaxBenefitMonthly();
        if (maxBenefitMonthly != null && maxBenefitMonthly > 0) {
            BigDecimal maxBenefit = BigDecimal.valueOf(maxBenefitMonthly);
            if (benefitAmount.compareTo(maxBenefit) > 0) {
                benefitAmount = maxBenefit;
            }
        }
        
        return benefitAmount.setScale(0, RoundingMode.HALF_UP);
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
     * 23개 표준 카테고리에 맞게 업데이트
     * @param category 카테고리명
     * @return 온라인 카테고리 여부
     */
    private static boolean isOnlineCategory(String category) {
        return "쇼핑".equals(category) || // 온라인쇼핑 포함
               "OTT/영화/문화".equals(category) || // 디지털구독 포함
               "간편결제".equals(category); // 온라인 결제 서비스
    }
    
    /**
     * 혜택 카테고리와 거래 카테고리가 매칭되는지 확인합니다.
     * CategoryMappingUtil을 사용하여 23개 표준 카테고리 체계로 매칭
     * @param benefitCategory 혜택 카테고리
     * @param transactionCategory 거래 카테고리 (MerchantCategoryService에서 분류된)
     * @return 매칭 여부
     */
    private static boolean isCategoryMatched(String benefitCategory, String transactionCategory) {
        if (benefitCategory == null || transactionCategory == null) {
            return false;
        }
        
        // CategoryMappingUtil을 사용하여 매칭 확인
        return CategoryMappingUtil.isCategoryMatch(benefitCategory, transactionCategory);
    }
    
    /**
     * 유사한 카테고리들을 매칭합니다.
     * @deprecated CategoryMappingUtil을 사용하여 23개 표준 카테고리로 대체됨
     * @param benefitCategory 혜택 카테고리
     * @param transactionCategory 거래 카테고리
     * @return 유사 카테고리 매칭 여부
     */
    @Deprecated
    private static boolean matchSimilarCategories(String benefitCategory, String transactionCategory) {
        // 이 메서드는 더 이상 사용되지 않음 - CategoryMappingUtil로 대체
        return CategoryMappingUtil.isCategoryMatch(benefitCategory, transactionCategory);
    }
}