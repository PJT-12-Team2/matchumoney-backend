package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CardBenefitDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.mapper.CardRecommendationMapper;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.PersonalizedRecommendationUtil;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.SmartBenefitCalculationUtil;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardParsedBenefitVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardProductVO;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.CardTransactionSummaryVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 개인화된 카드 추천 엔진
 * 사용자의 거래 패턴을 분석하여 맞춤형 카드를 추천합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalizedRecommendationEngine {
    
    private final CardRecommendationMapper cardRecommendationMapper;
    
    private static final int ANALYSIS_PERIOD_DAYS = 90; // 3개월 분석 기간
    
    /**
     * 사용자의 거래 패턴을 기반으로 개인화된 카드 추천을 제공합니다.
     */
    public List<CardBenefitDTO> recommendPersonalizedCards(Long userId, int limit) {
        log.info("🎯 사용자 {}의 개인화 카드 추천 시작 (최대 {}개)", userId, limit);
        
        try {
            // 1. 사용자 거래 패턴 분석 (최근 3개월)
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(ANALYSIS_PERIOD_DAYS);
            
            List<CardTransactionSummaryVO> transactionSummaries = 
                cardRecommendationMapper.selectTransactionSummaryByUserId(userId, startDate, endDate);
            
            if (transactionSummaries.isEmpty()) {
                log.warn("🚫 사용자 {}의 거래 내역이 없어 개인화 추천을 제공할 수 없습니다", userId);
                return Collections.emptyList();
            }
            
            log.info("📊 사용자 {}의 거래 패턴 분석 완료 - {} 개 카테고리", userId, transactionSummaries.size());
            
            // 2. 사용자 프로파일 생성
            PersonalizedRecommendationUtil.UserProfile userProfile = 
                PersonalizedRecommendationUtil.buildUserProfile(transactionSummaries);
            
            log.info("👤 사용자 프로파일 생성 완료 - 라이프스타일: {}, 주요카테고리: {}", 
                userProfile.getLifestyleType(), userProfile.getPrimaryCategories());
            
            // 3. 사용자 보유 카드 조회 (중복 추천 방지)
            Set<Integer> ownedCardIds = cardRecommendationMapper.selectUserOwnedCards(userId)
                .stream()
                .map(CardProductVO::getCardProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
            
            // 4. 추천 가능한 카드 목록 조회 (신용카드와 체크카드 모두)
            List<CardProductVO> creditCards = cardRecommendationMapper.selectAvailableCardsByType("신용", new ArrayList<>(ownedCardIds));
            List<CardProductVO> checkCards = cardRecommendationMapper.selectAvailableCardsByType("체크", new ArrayList<>(ownedCardIds));
            
            List<CardProductVO> availableCards = new ArrayList<>();
            availableCards.addAll(creditCards);
            availableCards.addAll(checkCards);
            
            log.info("🔍 분석 대상 카드: {} 개 (보유카드 {} 개 제외)", availableCards.size(), ownedCardIds.size());
            
            // 5. 각 카드에 대한 개인화 점수 계산
            List<PersonalizedRecommendationUtil.RecommendationResult> recommendations = 
                new ArrayList<>();
            
            for (CardProductVO card : availableCards) {
                PersonalizedRecommendationUtil.RecommendationResult result = 
                    analyzeCardForUser(userProfile, card, transactionSummaries, userId);
                
                if (result.getMatchScore() > 30.0) { // 최소 30점 이상만 추천
                    recommendations.add(result);
                }
            }
            
            // 6. 추천 점수 순으로 정렬 및 상위 결과 선택
            List<PersonalizedRecommendationUtil.RecommendationResult> topRecommendations = 
                recommendations.stream()
                .sorted((a, b) -> {
                    // 1차: 매칭 점수, 2차: 예상 혜택, 3차: 효율성 점수
                    int scoreCompare = Double.compare(b.getMatchScore(), a.getMatchScore());
                    if (scoreCompare != 0) return scoreCompare;
                    
                    int benefitCompare = b.getExpectedBenefit().compareTo(a.getExpectedBenefit());
                    if (benefitCompare != 0) return benefitCompare;
                    
                    return Double.compare(b.getEfficiencyScore(), a.getEfficiencyScore());
                })
                .limit(limit)
                .collect(Collectors.toList());
            
            // 7. CardBenefitDTO로 변환
            List<CardBenefitDTO> result = topRecommendations.stream()
                .map(rec -> convertToCardBenefitDTO(rec, userProfile))
                .collect(Collectors.toList());
            
            log.info("✅ 개인화 추천 완료 - {} 개 카드 추천", result.size());
            
            return result;
            
        } catch (Exception e) {
            log.error("❌ 개인화 카드 추천 중 오류 발생: 사용자 {}", userId, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 특정 카드가 사용자에게 얼마나 적합한지 분석합니다.
     */
    private PersonalizedRecommendationUtil.RecommendationResult analyzeCardForUser(
            PersonalizedRecommendationUtil.UserProfile userProfile,
            CardProductVO card,
            List<CardTransactionSummaryVO> transactionSummaries,
            Long userId) {
        
        PersonalizedRecommendationUtil.RecommendationResult result = 
            new PersonalizedRecommendationUtil.RecommendationResult();
        
        try {
            // 카드 정보 설정
            result.setCardId(card.getCardProductId().toString());
            result.setCardName(card.getName());
            
            // 카드 혜택 정보 조회
            List<CardParsedBenefitVO> cardBenefits = 
                cardRecommendationMapper.selectCardBenefitsByCardId(card.getCardProductId());
            
            if (cardBenefits.isEmpty()) {
                result.setMatchScore(0.0);
                return result;
            }
            
            // 매칭 점수 계산
            double matchScore = PersonalizedRecommendationUtil.calculateCardMatchScore(
                userProfile, cardBenefits, card);
            result.setMatchScore(matchScore);
            
            // 카테고리별 가중치 계산
            Map<String, Double> categoryWeights = 
                SmartBenefitCalculationUtil.calculateCategoryWeights(transactionSummaries);
            
            // 예상 혜택 계산
            Long totalSpending = Math.round(userProfile.getTotalMonthlySpending());
            BigDecimal expectedBenefit = SmartBenefitCalculationUtil.calculatePersonalizedBenefit(
                cardBenefits, transactionSummaries, totalSpending, card.getPreMonthMoney(), categoryWeights);
            result.setExpectedBenefit(expectedBenefit);
            
            // 효율성 점수 계산
            double efficiencyScore = SmartBenefitCalculationUtil.calculateBenefitEfficiencyScore(
                expectedBenefit, totalSpending, card.getAnnualFee());
            result.setEfficiencyScore(efficiencyScore);
            
            // 추천 이유 생성
            List<String> reasons = PersonalizedRecommendationUtil.generateRecommendationReasons(
                userProfile, cardBenefits, matchScore, expectedBenefit);
            result.setMatchReasons(reasons);
            
            // 상세 정보 설정
            Map<String, Object> details = new HashMap<>();
            details.put("annualFee", card.getAnnualFee());
            details.put("preMonthMoney", card.getPreMonthMoney());
            details.put("issuer", card.getIssuer());
            details.put("cardType", card.getType());
            details.put("benefitCount", cardBenefits.size());
            details.put("lifestyleMatch", userProfile.getLifestyleType());
            result.setDetails(details);
            
            log.debug("🔍 카드 분석 완료 - {}: 매칭점수={}, 예상혜택={}원", 
                card.getName(), Math.round(matchScore), expectedBenefit.intValue());
                
        } catch (Exception e) {
            log.warn("⚠️ 카드 분석 중 오류 발생 - {}: {}", card.getName(), e.getMessage());
            result.setMatchScore(0.0);
        }
        
        return result;
    }
    
    /**
     * RecommendationResult를 CardBenefitDTO로 변환합니다.
     */
    private CardBenefitDTO convertToCardBenefitDTO(
            PersonalizedRecommendationUtil.RecommendationResult recommendation,
            PersonalizedRecommendationUtil.UserProfile userProfile) {
        
        Map<String, Object> details = recommendation.getDetails();
        
        return CardBenefitDTO.builder()
            .cardId(Integer.valueOf(recommendation.getCardId()))
            .cardName(recommendation.getCardName())
            .cardType((String) details.get("cardType"))
            .issuer((String) details.get("issuer"))
            .estimatedBenefit(recommendation.getExpectedBenefit().longValue())
            .annualFee((String) details.get("annualFee"))
            .preMonthMoney((Long) details.get("preMonthMoney"))
            // 추천 시스템 고도화 필드
            .recommendationScore(recommendation.getMatchScore())
            .expectedMonthlyBenefit(recommendation.getExpectedBenefit())
            .expectedYearlyBenefit(recommendation.getExpectedBenefit().multiply(BigDecimal.valueOf(12)))
            .netBenefit(calculateNetBenefit(recommendation.getExpectedBenefit(), (String) details.get("annualFee")))
            .recommendationReasons(recommendation.getMatchReasons())
            .conditionFulfillmentProbability(calculateConditionFulfillment(userProfile, (Long) details.get("preMonthMoney")))
            .expectedAchievementRate(calculateAchievementRate(userProfile, (Long) details.get("preMonthMoney")))
            .mainBenefitCategories(userProfile.getPrimaryCategories())
            // 카테고리별 혜택은 별도 계산 필요
            .categoryBenefits(new HashMap<>())
            .build();
    }
    
    /**
     * 순 혜택을 계산합니다 (연 혜택 - 연회비).
     */
    private BigDecimal calculateNetBenefit(BigDecimal monthlyBenefit, String annualFee) {
        BigDecimal yearlyBenefit = monthlyBenefit.multiply(BigDecimal.valueOf(12));
        
        if (annualFee == null || "무료".equals(annualFee)) {
            return yearlyBenefit;
        }
        
        try {
            String feeNumeric = annualFee.replaceAll("[^0-9]", "");
            if (!feeNumeric.isEmpty()) {
                BigDecimal fee = new BigDecimal(feeNumeric);
                return yearlyBenefit.subtract(fee);
            }
        } catch (NumberFormatException e) {
            log.debug("연회비 파싱 실패: {}", annualFee);
        }
        
        return yearlyBenefit;
    }
    
    /**
     * 조건 충족 확률을 계산합니다.
     */
    private Double calculateConditionFulfillment(PersonalizedRecommendationUtil.UserProfile userProfile, Long preMonthMoney) {
        if (preMonthMoney == null || preMonthMoney <= 0) {
            return 1.0; // 전월 실적 조건 없음
        }
        
        double userSpending = userProfile.getTotalMonthlySpending();
        if (userSpending >= preMonthMoney) {
            return 1.0; // 조건 충족
        }
        
        double ratio = userSpending / preMonthMoney;
        return Math.max(0.3, ratio); // 최소 30% 확률 보장
    }
    
    /**
     * 달성률을 계산합니다.
     */
    private Double calculateAchievementRate(PersonalizedRecommendationUtil.UserProfile userProfile, Long preMonthMoney) {
        if (preMonthMoney == null || preMonthMoney <= 0) {
            return 1.0;
        }
        
        double userSpending = userProfile.getTotalMonthlySpending();
        return Math.min(1.0, userSpending / preMonthMoney);
    }
    
    /**
     * 사용자의 소비 패턴을 분석하여 인사이트를 제공합니다.
     */
    public Map<String, Object> analyzeUserSpendingPattern(Long userId) {
        log.info("📈 사용자 {}의 소비 패턴 분석 시작", userId);
        
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(ANALYSIS_PERIOD_DAYS);
            
            List<CardTransactionSummaryVO> transactionSummaries = 
                cardRecommendationMapper.selectTransactionSummaryByUserId(userId, startDate, endDate);
            
            if (transactionSummaries.isEmpty()) {
                analysis.put("hasData", false);
                analysis.put("message", "분석할 거래 내역이 없습니다");
                return analysis;
            }
            
            // 사용자 프로파일 생성
            PersonalizedRecommendationUtil.UserProfile userProfile = 
                PersonalizedRecommendationUtil.buildUserProfile(transactionSummaries);
            
            // 기본 통계
            analysis.put("hasData", true);
            analysis.put("totalMonthlySpending", userProfile.getTotalMonthlySpending());
            analysis.put("lifestyleType", userProfile.getLifestyleType());
            analysis.put("primaryCategories", userProfile.getPrimaryCategories());
            analysis.put("categoryCount", transactionSummaries.size());
            
            // 카테고리별 상세 분석
            Map<String, Object> categoryAnalysis = new HashMap<>();
            for (CardTransactionSummaryVO summary : transactionSummaries) {
                Map<String, Object> categoryData = new HashMap<>();
                categoryData.put("amount", summary.getTotalAmount());
                categoryData.put("transactions", summary.getTransactionCount());
                categoryData.put("avgAmount", summary.getAverageAmount());
                categoryData.put("ratio", summary.getCategoryRatio());
                categoryData.put("preference", userProfile.getCategoryPreferences().get(summary.getCategory()));
                
                categoryAnalysis.put(summary.getCategory(), categoryData);
            }
            analysis.put("categoryAnalysis", categoryAnalysis);
            
            // 추천 인사이트
            List<String> insights = generateSpendingInsights(userProfile, transactionSummaries);
            analysis.put("insights", insights);
            
            log.info("✅ 소비 패턴 분석 완료 - 사용자 {}: {}", userId, userProfile.getLifestyleType());
            
        } catch (Exception e) {
            log.error("❌ 소비 패턴 분석 중 오류 발생: 사용자 {}", userId, e);
            analysis.put("hasData", false);
            analysis.put("error", "분석 중 오류가 발생했습니다");
        }
        
        return analysis;
    }
    
    /**
     * 소비 패턴 기반 인사이트를 생성합니다.
     */
    private List<String> generateSpendingInsights(
            PersonalizedRecommendationUtil.UserProfile userProfile,
            List<CardTransactionSummaryVO> transactionSummaries) {
        
        List<String> insights = new ArrayList<>();
        
        // 가장 많이 사용하는 카테고리
        if (!userProfile.getPrimaryCategories().isEmpty()) {
            String topCategory = userProfile.getPrimaryCategories().get(0);
            Double topPreference = userProfile.getCategoryPreferences().get(topCategory);
            insights.add(String.format("가장 많이 사용하는 카테고리는 '%s'로 전체의 %.1f%%를 차지합니다", 
                topCategory, topPreference * 100));
        }
        
        // 라이프스타일 기반 인사이트
        String lifestyleType = userProfile.getLifestyleType();
        switch (lifestyleType) {
            case "프리미엄_여행족":
                insights.add("여행을 자주 다니시는 분으로, 항공마일리지와 해외 혜택이 있는 카드를 추천합니다");
                break;
            case "외식_선호형":
                insights.add("외식을 즐기시는 분으로, 음식점과 카페 할인 혜택이 있는 카드가 적합합니다");
                break;
            case "절약_실용형":
                insights.add("실용적인 소비를 하시는 분으로, 마트와 생활비 할인 카드를 추천합니다");
                break;
            default:
                insights.add("균형잡힌 소비 패턴을 보이시며, 다양한 카테고리 혜택이 있는 카드가 적합합니다");
        }
        
        // 개선 제안
        double totalSpending = userProfile.getTotalMonthlySpending();
        if (totalSpending >= 1000000) {
            insights.add("월 사용액이 높아 프리미엄 카드의 혜택을 충분히 활용하실 수 있습니다");
        } else if (totalSpending >= 500000) {
            insights.add("중간 수준의 사용액으로 연회비가 있는 카드도 고려해볼 만합니다");
        } else {
            insights.add("연회비 무료 카드로 시작하여 점진적으로 업그레이드하는 것을 추천합니다");
        }
        
        return insights;
    }
}