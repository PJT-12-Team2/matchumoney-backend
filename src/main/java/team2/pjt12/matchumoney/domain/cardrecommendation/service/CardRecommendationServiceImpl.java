package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.*;
import team2.pjt12.matchumoney.domain.cardrecommendation.mapper.CardRecommendationMapper;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.UserCardRecommendationService;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.BenefitCalculationUtil;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.CategoryMappingUtil;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.SmartBenefitCalculationUtil;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.PersonalizedRecommendationUtil;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.PersonalizedRecommendationEngine;
import team2.pjt12.matchumoney.domain.mydata.service.MerchantCategoryService;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.*;
import team2.pjt12.matchumoney.domain.carddetail.mapper.CardDetailMapper;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardRecommendationServiceImpl implements CardRecommendationService {

    // 카드 추천 분석 기간 (일)
    private static final int ANALYSIS_PERIOD_DAYS = 30;

    private final CardRecommendationMapper cardRecommendationMapper;
    private final UserCardRecommendationService userCardRecommendationService;
    private final MerchantCategoryService merchantCategoryService;
    private final CardDetailMapper cardDetailMapper;
    private final UserMapper userMapper;
    private final PersonalizedRecommendationEngine personalizedRecommendationEngine;

    @Override
    public MyCardBenefitResponseDTO calculateSpecificCardBenefit(Long userId, Integer cardId) {
        log.info("🔍 사용자 {}의 특정 카드 {} 혜택 계산 시작", userId, cardId);

        // 분석 기간 설정 (최근 30일)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(ANALYSIS_PERIOD_DAYS);

        // 카드 정보 조회 (cardId가 실제로는 cardProductId 또는 holdingId일 수 있음)
        CardProductVO targetCard = null;
        List<CardProductVO> ownedCards = cardRecommendationMapper.selectUserOwnedCards(userId);
        
        // cardId로 매칭되는 카드 찾기
        for (CardProductVO card : ownedCards) {
            if (cardId.equals(card.getCardProductId()) || 
                (card.getCardProductId() == null && cardId.equals(card.getHoldingId().intValue()))) {
                targetCard = card;
                break;
            }
        }
        
        if (targetCard == null) {
            log.warn("사용자 {}의 카드 {}를 찾을 수 없습니다.", userId, cardId);
            return MyCardBenefitResponseDTO.builder()
                .totalSpendAmount(0L)
                .categoryStats(Collections.emptyList())
                .ownedCardBenefits(Collections.emptyList())
                .build();
        }
        
        // 거래내역 통계 조회 (매칭된 카드는 cardId, 매칭되지 않은 카드는 holdingId 사용)
        log.info("🔍 거래내역 통계 조회 - userId: {}, cardId: {}, holdingId: {}, 기간: {} ~ {}", 
            userId, cardId, targetCard.getHoldingId(), startDate, endDate);
            
        List<CardTransactionSummaryVO> transactionSummaries;
        Long totalSpendAmount;
        
        if (targetCard.getCardProductId() != null) {
            // 매칭된 카드: 기존 로직 사용
            transactionSummaries = cardRecommendationMapper.selectTransactionSummaryByUserAndCard(
                userId, targetCard.getCardProductId(), startDate, endDate);
            totalSpendAmount = cardRecommendationMapper.selectTotalSpendByUserAndCard(
                userId, targetCard.getCardProductId(), startDate, endDate);
        } else {
            // 매칭되지 않은 카드: holdingId 기준 조회
            transactionSummaries = cardRecommendationMapper.selectTransactionSummaryByHoldingId(
                userId, targetCard.getHoldingId(), startDate, endDate);
            totalSpendAmount = cardRecommendationMapper.selectTotalSpendByHoldingId(
                userId, targetCard.getHoldingId(), startDate, endDate);
        }

        log.info("📊 거래내역 통계 조회 완료 - 카테고리 수: {}", transactionSummaries.size());
        if (transactionSummaries.isEmpty()) {
            log.warn("⚠️ 사용자 {}의 카드 {}에 대한 최근 {}일 거래 내역이 없습니다.", userId, cardId, ANALYSIS_PERIOD_DAYS);
            return MyCardBenefitResponseDTO.builder()
                .totalSpendAmount(0L)
                .categoryStats(Collections.emptyList())
                .ownedCardBenefits(Collections.emptyList())
                .build();
        }

        // 카드 혜택 계산 (매칭된 카드만 혜택 계산 가능)
        List<CardBenefitDTO> cardBenefits = new ArrayList<>();
        
        if (targetCard.getCardProductId() != null) {
            // 매칭된 카드: 혜택 계산
            List<CardParsedBenefitVO> benefits = 
                cardRecommendationMapper.selectCardBenefitsByCardId(targetCard.getCardProductId());

            BigDecimal totalBenefit = BenefitCalculationUtil.calculateTotalBenefit(
                benefits, transactionSummaries, totalSpendAmount, targetCard.getPreMonthMoney());

            cardBenefits.add(CardBenefitDTO.builder()
                .cardId(targetCard.getCardProductId())
                .cardName(targetCard.getName())
                .cardType(targetCard.getType())
                .issuer(targetCard.getIssuer())
                .estimatedBenefit(totalBenefit.longValue())
                .annualFee(targetCard.getAnnualFee())
                .preMonthMoney(targetCard.getPreMonthMoney())
                .cardImageUrl(targetCard.getCardImageUrl())
                .requestPcUrl(targetCard.getRequestPcUrl())
                .requestMobileUrl(targetCard.getRequestMobileUrl())
                .build()
            );
            
            log.info("✅ 매칭된 카드 혜택 계산 완료 - 카드: '{}', 예상 혜택: {}원", 
                targetCard.getName(), totalBenefit);
        } else {
            // 매칭되지 않은 카드: 거래내역 통계만 제공
            log.info("⚠️ 매칭되지 않은 카드 - 카드: '{}', 거래내역만 제공", targetCard.getName());
            cardBenefits.add(CardBenefitDTO.builder()
                .cardId(targetCard.getHoldingId().intValue()) // holdingId를 임시 ID로 사용
                .cardName(targetCard.getName())
                .cardType(targetCard.getType())
                .issuer(targetCard.getIssuer())
                .estimatedBenefit(0L) // 매칭되지 않아 혜택 계산 불가
                .annualFee("알 수 없음")
                .preMonthMoney(null)
                .cardImageUrl(null)
                .requestPcUrl(null)
                .requestMobileUrl(null)
                .build()
            );
        }

        long totalBenefitAmount = cardBenefits.stream()
            .mapToLong(benefit -> benefit.getEstimatedBenefit() != null ? benefit.getEstimatedBenefit() : 0L)
            .sum();
        log.info("사용자 {}의 카드 {} 혜택 계산 완료. 예상 혜택: {}원", userId, cardId, totalBenefitAmount);

        return MyCardBenefitResponseDTO.builder()
            .totalSpendAmount(totalSpendAmount)
            .categoryStats(transactionSummaries.stream()
                .map(this::convertToCategoryStatDTO)
                .collect(Collectors.toList()))
            .ownedCardBenefits(cardBenefits)
            .build();
    }

    @Override
    public CardRecommendationResponseDTO recommendBetterCards(Long userId, Integer cardId) {
        log.info("사용자 {}의 카드 {} 기준 더 나은 카드 추천 서비스 시작", userId, cardId);

        // 분석 기간 설정 (최근 30일)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(ANALYSIS_PERIOD_DAYS);

        // 카드 정보 조회
        CardProductVO targetCard = null;
        List<CardProductVO> ownedCards = cardRecommendationMapper.selectUserOwnedCards(userId);
        
        for (CardProductVO card : ownedCards) {
            if (cardId.equals(card.getCardProductId()) || 
                (card.getCardProductId() == null && cardId.equals(card.getHoldingId().intValue()))) {
                targetCard = card;
                break;
            }
        }
        
        if (targetCard == null) {
            log.warn("사용자 {}의 카드 {}를 찾을 수 없습니다.", userId, cardId);
            return CardRecommendationResponseDTO.builder()
                .totalSpendAmount(0L)
                .categoryStats(Collections.emptyList())
                .recommendedCards(Collections.emptyList())
                .message("카드 정보를 찾을 수 없습니다.")
                .build();
        }

        // 거래내역 통계 조회
        List<CardTransactionSummaryVO> transactionSummaries;
        Long totalSpendAmount;
        
        if (targetCard.getCardProductId() != null) {
            transactionSummaries = cardRecommendationMapper.selectTransactionSummaryByUserAndCard(
                userId, targetCard.getCardProductId(), startDate, endDate);
            totalSpendAmount = cardRecommendationMapper.selectTotalSpendByUserAndCard(
                userId, targetCard.getCardProductId(), startDate, endDate);
        } else {
            transactionSummaries = cardRecommendationMapper.selectTransactionSummaryByHoldingId(
                userId, targetCard.getHoldingId(), startDate, endDate);
            totalSpendAmount = cardRecommendationMapper.selectTotalSpendByHoldingId(
                userId, targetCard.getHoldingId(), startDate, endDate);
        }

        if (transactionSummaries.isEmpty()) {
            String cardName = targetCard.getName();
            log.warn("사용자 {}의 카드 '{}'에 대한 최근 {}일 거래 내역이 없어 추천이 어렵습니다.", userId, cardName, ANALYSIS_PERIOD_DAYS);
            return CardRecommendationResponseDTO.builder()
                .totalSpendAmount(0L)
                .categoryStats(Collections.emptyList())
                .recommendedCards(Collections.emptyList())
                .message(String.format("카드 '%s'의 최근 %d일 거래 내역이 없어 추천이 어렵습니다. " +
                        "카드를 사용하신 후 거래내역을 업데이트해 주세요.", cardName, ANALYSIS_PERIOD_DAYS))
                .build();
        }

        // 매칭되지 않은 카드의 경우 거래내역 기반으로만 추천
        if (targetCard.getCardProductId() == null) {
            log.info("🔍 매칭되지 않은 카드 '{}'에 대한 거래내역 기반 추천 시작", targetCard.getName());
            
            // 매칭되지 않은 카드는 거래내역 패턴만으로 추천
            List<CardProductVO> allAvailableCards = cardRecommendationMapper.selectAvailableCardsByType("신용", Collections.emptyList());
            
            // 거래 패턴과 유사한 카드들을 추천 (간단한 버전)
            List<CardBenefitDTO> recommendedCards = allAvailableCards.stream()
                .limit(5) // 상위 5개만
                .map(card -> CardBenefitDTO.builder()
                    .cardId(card.getCardProductId())
                    .cardName(card.getName())
                    .cardType(card.getType())
                    .issuer(card.getIssuer())
                    .estimatedBenefit(0L) // 정확한 계산 불가
                    .annualFee(card.getAnnualFee())
                    .preMonthMoney(card.getPreMonthMoney())
                    .cardImageUrl(card.getCardImageUrl())
                    .requestPcUrl(card.getRequestPcUrl())
                    .requestMobileUrl(card.getRequestMobileUrl())
                    .recommendationReasons(java.util.Arrays.asList("거래내역 패턴을 기반으로 추천된 카드입니다"))
                    .build())
                .collect(Collectors.toList());
                
            String message = String.format("카드 '%s'는 매칭되지 않아 정확한 혜택 비교는 어렵지만, " +
                    "거래내역 패턴을 기반으로 %d개 카드를 추천합니다.", 
                    targetCard.getName(), recommendedCards.size());
                    
            return CardRecommendationResponseDTO.builder()
                .totalSpendAmount(totalSpendAmount)
                .categoryStats(transactionSummaries.stream()
                    .map(this::convertToCategoryStatDTO)
                    .collect(Collectors.toList()))
                .recommendedCards(recommendedCards)
                .message(message)
                .build();
        }

        // 매칭된 카드의 기존 추천 로직
        List<CardParsedBenefitVO> baseBenefits = 
            cardRecommendationMapper.selectCardBenefitsByCardId(targetCard.getCardProductId());
        BigDecimal baseCardBenefit = BenefitCalculationUtil.calculateTotalBenefit(
            baseBenefits, transactionSummaries, totalSpendAmount, targetCard.getPreMonthMoney());

        log.info("기준 카드 {} 혜택: {}원", cardId, baseCardBenefit);
        
        // 거래 금액이 10만원 이하인 경우 개인화된 카테고리 중심 추천으로 전환
        boolean isLowSpendingUser = totalSpendAmount != null && totalSpendAmount <= 100000;
        if (isLowSpendingUser) {
            log.info("💡 저액 사용자({})로 판단 - 카테고리 중심 개인화 추천으로 전환", totalSpendAmount);
            return recommendCardsForLowSpendingUser(userId, targetCard, transactionSummaries, totalSpendAmount);
        }

        // 같은 타입의 카드들 조회 (기준 카드 제외)
        log.info("기준 카드 타입: {} - 같은 타입 카드만 추천합니다", targetCard.getType());
        List<CardProductVO> availableCards = cardRecommendationMapper
            .selectAvailableCardsByType(targetCard.getType(), Collections.singletonList(targetCard.getCardProductId()));

        List<CardBenefitDTO> betterCards = new ArrayList<>();

        // 고도화된 혜택 계산을 위한 카테고리 가중치 계산
        Map<String, Double> categoryWeights = SmartBenefitCalculationUtil.calculateCategoryWeights(transactionSummaries);
        
        // 각 카드별 혜택 계산하여 기준 카드보다 나은 카드 찾기
        for (CardProductVO card : availableCards) {
            List<CardParsedBenefitVO> benefits = 
                cardRecommendationMapper.selectCardBenefitsByCardId(card.getCardProductId());

            // 고도화된 개인화 혜택 계산 사용
            BigDecimal cardBenefit = SmartBenefitCalculationUtil.calculatePersonalizedBenefit(
                benefits, transactionSummaries, totalSpendAmount, card.getPreMonthMoney(), categoryWeights);

            // 기준 카드보다 혜택이 더 큰 카드만 추천
            if (cardBenefit.compareTo(baseCardBenefit) > 0) {
                // 개선된 추천 점수 계산
                Double recommendationScore = calculateEnhancedRecommendationScore(
                    cardBenefit, baseCardBenefit, benefits, transactionSummaries, card, categoryWeights);
                
                // 고도화된 카테고리별 혜택 계산
                Map<String, BigDecimal> categoryBenefits = SmartBenefitCalculationUtil.analyzeCategoryBenefits(
                    benefits, transactionSummaries, totalSpendAmount, categoryWeights)
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (BigDecimal) ((Map<String, Object>) entry.getValue()).get("expectedBenefit")
                    ));
                
                // 개선된 추천 이유 생성
                List<String> recommendationReasons = generateEnhancedRecommendationReasons(
                    cardBenefit, baseCardBenefit, categoryBenefits, categoryWeights);
                
                // 주요 혜택 카테고리 추출 (가중치 고려)
                List<String> mainBenefitCategories = extractWeightedBenefitCategories(categoryBenefits, categoryWeights, 3);
                
                // 효율성 점수 계산
                double efficiencyScore = SmartBenefitCalculationUtil.calculateBenefitEfficiencyScore(
                    cardBenefit, totalSpendAmount, card.getAnnualFee());
                
                betterCards.add(CardBenefitDTO.builder()
                    .cardId(card.getCardProductId())
                    .cardName(card.getName())
                    .cardType(card.getType())
                    .issuer(card.getIssuer())
                    .estimatedBenefit(cardBenefit.longValue())
                    .annualFee(card.getAnnualFee())
                    .preMonthMoney(card.getPreMonthMoney())
                    .cardImageUrl(card.getCardImageUrl())
                    .requestPcUrl(card.getRequestPcUrl())
                    .requestMobileUrl(card.getRequestMobileUrl())
                    // 고도화된 추천 필드들
                    .recommendationScore(recommendationScore)
                    .expectedMonthlyBenefit(cardBenefit)
                    .expectedYearlyBenefit(cardBenefit.multiply(BigDecimal.valueOf(12)))
                    .netBenefit(calculateNetBenefit(cardBenefit.multiply(BigDecimal.valueOf(12)), card.getAnnualFee()))
                    .categoryBenefits(categoryBenefits)
                    .recommendationReasons(recommendationReasons)
                    .conditionFulfillmentProbability(calculateConditionFulfillmentProbability(card, totalSpendAmount))
                    .expectedAchievementRate(calculateAchievementRate(card, totalSpendAmount))
                    .mainBenefitCategories(mainBenefitCategories)
                    .build());
                
                log.debug("💡 카드 추천 계산 완료 - {}: 혜택={}원, 점수={}, 효율성={}", 
                    card.getName(), cardBenefit.intValue(), Math.round(recommendationScore), Math.round(efficiencyScore));
            }
        }

        // 추천 점수 순으로 정렬하고 상위 5개만 선택
        List<CardBenefitDTO> topRecommendations = betterCards.stream()
            .sorted((a, b) -> {
                // 추천 점수가 있으면 추천 점수로, 없으면 혜택 금액으로 정렬
                if (a.getRecommendationScore() != null && b.getRecommendationScore() != null) {
                    return b.getRecommendationScore().compareTo(a.getRecommendationScore());
                } else {
                    return b.getEstimatedBenefit().compareTo(a.getEstimatedBenefit());
                }
            })
            .limit(5)
            .collect(Collectors.toList());

        String message;
        if (topRecommendations.isEmpty()) {
            message = String.format("현재 카드(%s, 예상혜택: %s원)가 해당 소비 패턴에 가장 적합합니다.", 
                targetCard.getName(), baseCardBenefit.toString());
        } else {
            Long maxBenefit = topRecommendations.get(0).getEstimatedBenefit();
            Long benefitDiff = maxBenefit - baseCardBenefit.longValue();
            message = String.format("더 나은 혜택을 제공하는 %d개 카드를 찾았습니다. 최대 %s원 더 혜택을 받을 수 있습니다.", 
                topRecommendations.size(), benefitDiff.toString());
        }

        // 추천 결과를 데이터베이스에 저장
        try {
            if (!topRecommendations.isEmpty()) {
                userCardRecommendationService.saveRecommendations(userId, cardId, topRecommendations);
                log.info("사용자 {}의 카드 {} 기준 추천 결과 데이터베이스 저장 완료", userId, cardId);
            }
        } catch (Exception e) {
            log.warn("추천 결과 저장 중 오류 발생하였으나 응답은 정상 처리: 사용자 {}, 카드 {}", userId, cardId, e);
            // 저장 실패해도 추천 결과는 정상 반환
        }

        log.info("사용자 {}의 카드 {} 기준 추천 완료. 총 {}개 더 나은 카드 발견", userId, cardId, topRecommendations.size());

        // 각 추천 카드에 좋아요/즐겨찾기 상태 설정
        topRecommendations.forEach(cardDto -> setLikeAndFavoriteStatus(userId, cardDto));
        
        return CardRecommendationResponseDTO.builder()
            .totalSpendAmount(totalSpendAmount)
            .categoryStats(transactionSummaries.stream()
                .map(this::convertToCategoryStatDTO)
                .collect(Collectors.toList()))
            .recommendedCards(topRecommendations)
            .message(message)
            .build();
    }

    /**
     * CardTransactionSummaryVO를 CategoryStatDTO로 변환합니다.
     */
    private CategoryStatDTO convertToCategoryStatDTO(CardTransactionSummaryVO summary) {
        return CategoryStatDTO.builder()
            .category(summary.getCategory())
            .totalAmount(summary.getTotalAmount())
            .transactionCount(summary.getTransactionCount())
            .averageAmount(summary.getAverageAmount())
            .categoryRatio(summary.getCategoryRatio())
            .build();
    }

    @Override
    public List<MyCardBenefitResponseDTO> getMyCardsBenefits(Long userId) {
        log.info("사용자 {}의 모든 보유 카드 혜택 조회 시작", userId);

        // 사용자가 보유한 카드 목록 조회
        List<CardProductVO> ownedCards = cardRecommendationMapper.selectUserOwnedCards(userId);
        
        if (ownedCards.isEmpty()) {
            log.warn("사용자 {}의 보유 카드가 없습니다.", userId);
            return Collections.emptyList();
        }

        List<MyCardBenefitResponseDTO> myCardsBenefits = new ArrayList<>();

        for (CardProductVO card : ownedCards) {
            try {
                Integer cardIdForCalculation = card.getCardProductId() != null ? 
                    card.getCardProductId() : card.getHoldingId().intValue();
                    
                log.info("🔍 카드 혜택 계산 시작 - 카드: '{}', cardProductId: {}, holdingId: {}", 
                    card.getName(), card.getCardProductId(), card.getHoldingId());
                    
                MyCardBenefitResponseDTO cardBenefit = calculateSpecificCardBenefit(userId, cardIdForCalculation);
                myCardsBenefits.add(cardBenefit);
                
                long totalBenefit = cardBenefit.getOwnedCardBenefits().stream()
                    .mapToLong(benefit -> benefit.getEstimatedBenefit() != null ? benefit.getEstimatedBenefit() : 0L)
                    .sum();
                    
                String matchStatus = card.getCardProductId() != null ? "매칭됨" : "매칭되지 않음";
                log.info("✅ 카드 혜택 계산 완료 - 카드: '{}' ({}), 총혜택: {}원, 카테고리: {}개", 
                    card.getName(), matchStatus, totalBenefit, cardBenefit.getCategoryStats().size());
            } catch (Exception e) {
                log.warn("⚠️ 사용자 {}의 카드 '{}' 혜택 계산 중 오류 발생, 건너뜁니다: {}", 
                    userId, card.getName(), e.getMessage());
            }
        }

        log.info("사용자 {}의 보유 카드 {} 개 중 {} 개 카드 혜택 조회 완료", 
            userId, ownedCards.size(), myCardsBenefits.size());
        
        return myCardsBenefits;
    }

    @Override
    public CardRecommendationResponseDTO getSavedRecommendations(Long userId, Integer cardId) {
        log.info("사용자 {}의 카드 {} 저장된 추천 조회 시작", userId, cardId);

        try {
            // 저장된 추천 데이터 조회
            List<CardBenefitDTO> savedRecommendations = 
                userCardRecommendationService.getSavedRecommendations(userId, cardId);

            // 카드별 거래 통계 조회 (최근 30일)
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(ANALYSIS_PERIOD_DAYS);
            
            List<CardTransactionSummaryVO> transactionSummaries = 
                cardRecommendationMapper.selectTransactionSummaryByUserAndCard(userId, cardId, startDate, endDate);
            
            Long totalSpendAmount = cardRecommendationMapper.selectTotalSpendByUserAndCard(userId, cardId, startDate, endDate);

            String message = savedRecommendations.isEmpty() ? 
                "저장된 추천 데이터가 없습니다. 거래내역을 먼저 동기화해주세요." :
                String.format("저장된 추천 카드 %d개를 조회했습니다.", savedRecommendations.size());

            return CardRecommendationResponseDTO.builder()
                .totalSpendAmount(totalSpendAmount)
                .categoryStats(transactionSummaries.stream()
                    .map(this::convertToCategoryStatDTO)
                    .collect(Collectors.toList()))
                .recommendedCards(savedRecommendations)
                .message(message)
                .build();

        } catch (Exception e) {
            log.error("저장된 추천 조회 중 오류 발생: 사용자 {}, 카드 {}", userId, cardId, e);
            throw new RuntimeException("저장된 추천 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public void generateAndSaveRecommendations(Long userId, Integer cardId) {
        log.info("사용자 {}의 카드 {} 추천 자동 생성 및 저장 시작", userId, cardId);

        try {
            // 추천 카드 계산 및 저장 (기존 recommendBetterCards 로직 사용)
            CardRecommendationResponseDTO recommendations = recommendBetterCards(userId, cardId);
            
            log.info("사용자 {}의 카드 {} 추천 자동 생성 완료. {} 개 카드 추천", 
                userId, cardId, recommendations.getRecommendedCards().size());

        } catch (Exception e) {
            log.error("추천 자동 생성 중 오류 발생: 사용자 {}, 카드 {}", userId, cardId, e);
            // 오류가 발생해도 거래내역 저장에는 영향을 주지 않도록 예외를 다시 던지지 않음
        }
    }

    /**
     * UserCardRecommendationVO를 CardBenefitDTO로 변환합니다.
     */
    private CardBenefitDTO convertToCardBenefitDTO(UserCardRecommendationVO vo) {
        return CardBenefitDTO.builder()
            .cardId(vo.getCardId())
            .cardName(vo.getCardName())
            .cardType(vo.getCardType())
            .issuer(vo.getIssuer())
            .estimatedBenefit(vo.getEstimatedBenefit())
            .annualFee(vo.getAnnualFee())
            .preMonthMoney(vo.getPreMonthMoney())
            .cardImageUrl(vo.getCardImageUrl())
            .requestPcUrl(vo.getRequestPcUrl())
            .requestMobileUrl(vo.getRequestMobileUrl())
            // 기본값 설정
            .recommendationScore(75.0)
            .expectedMonthlyBenefit(BigDecimal.valueOf(vo.getEstimatedBenefit()).divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP))
            .expectedYearlyBenefit(BigDecimal.valueOf(vo.getEstimatedBenefit()))
            .netBenefit(calculateNetBenefit(BigDecimal.valueOf(vo.getEstimatedBenefit()), vo.getAnnualFee()))
            .categoryBenefits(new java.util.HashMap<>())
            .recommendationReasons(java.util.Arrays.asList("저장된 추천 데이터입니다"))
            .conditionFulfillmentProbability(0.8)
            .expectedAchievementRate(0.8)
            .mainBenefitCategories(new java.util.ArrayList<>())
            .build();
    }
    
    /**
     * 추천 점수를 계산합니다.
     */
    private Double calculateRecommendationScore(BigDecimal cardBenefit, BigDecimal baseBenefit,
                                               List<CardParsedBenefitVO> benefits, 
                                               List<CardTransactionSummaryVO> transactionSummaries,
                                               CardProductVO card) {
        // 혜택 금액 비율 (0-40점)
        double benefitScore = Math.min(40, cardBenefit.subtract(baseBenefit)
            .divide(BigDecimal.valueOf(10000), 2, java.math.RoundingMode.HALF_UP).doubleValue());
        
        // 카테고리 매칭 점수 (0-30점)
        double categoryMatchScore = calculateCategoryMatchScore(benefits, transactionSummaries);
        
        // 조건 충족 점수 (0-20점)
        double conditionScore = calculateConditionFulfillmentProbability(card, 
            transactionSummaries.stream().mapToLong(CardTransactionSummaryVO::getTotalAmount).sum()) * 20;
        
        // 활용도 점수 (0-10점)
        double utilizationScore = 10.0; // 기본 활용도
        
        return Math.max(0, Math.min(100, benefitScore + categoryMatchScore + conditionScore + utilizationScore));
    }
    
    /**
     * 카테고리 매칭 점수를 계산합니다.
     */
    private double calculateCategoryMatchScore(List<CardParsedBenefitVO> benefits, 
                                              List<CardTransactionSummaryVO> transactionSummaries) {
        int matchedCategories = 0;
        int totalTransactionCategories = transactionSummaries.size();
        
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            String transactionCategory = summary.getCategory();
            
            for (CardParsedBenefitVO benefit : benefits) {
                String benefitTransactionCategory = CategoryMappingUtil.mapBenefitToTransactionCategory(benefit.getCategory());
                if (transactionCategory.equals(benefitTransactionCategory)) {
                    matchedCategories++;
                    break;
                }
            }
        }
        
        if (totalTransactionCategories == 0) return 0;
        return (double) matchedCategories / totalTransactionCategories * 30;
    }
    
    /**
     * 카테고리별 혜택 금액을 계산합니다.
     * 개선된 BenefitCalculationUtil의 메서드를 사용하여 월 한도를 정확히 적용합니다.
     */
    private Map<String, BigDecimal> calculateCategoryBenefits(List<CardParsedBenefitVO> benefits,
                                                             List<CardTransactionSummaryVO> transactionSummaries,
                                                             Long totalSpendAmount,
                                                             Long cardPreMonthMoney) {
        // BenefitCalculationUtil의 개선된 카테고리별 혜택 계산 메서드 사용
        return BenefitCalculationUtil.calculateCategoryBenefits(
            benefits, transactionSummaries, totalSpendAmount, cardPreMonthMoney);
    }
    
    /**
     * 순 혜택을 계산합니다.
     */
    private BigDecimal calculateNetBenefit(BigDecimal yearlyBenefit, String annualFeeStr) {
        try {
            if (annualFeeStr == null || annualFeeStr.trim().isEmpty() || "무료".equals(annualFeeStr)) {
                return yearlyBenefit;
            }
            
            // 연회비에서 숫자만 추출
            String feeNumeric = annualFeeStr.replaceAll("[^0-9]", "");
            if (feeNumeric.isEmpty()) {
                return yearlyBenefit;
            }
            
            BigDecimal annualFee = new BigDecimal(feeNumeric);
            return yearlyBenefit.subtract(annualFee);
        } catch (Exception e) {
            log.warn("연회비 파싱 오류: {}", annualFeeStr, e);
            return yearlyBenefit;
        }
    }
    
    /**
     * 조건 충족 가능성을 계산합니다.
     */
    private Double calculateConditionFulfillmentProbability(CardProductVO card, Long userSpending) {
        if (card.getPreMonthMoney() == null || card.getPreMonthMoney() == 0) {
            return 1.0; // 전월 실적 조건이 없으면 100%
        }
        
        if (userSpending >= card.getPreMonthMoney()) {
            return 1.0; // 조건 충족
        } else {
            double ratio = (double) userSpending / card.getPreMonthMoney();
            return Math.max(0.3, ratio); // 최소 30%는 보장
        }
    }
    
    /**
     * 달성률을 계산합니다.
     */
    private Double calculateAchievementRate(CardProductVO card, Long userSpending) {
        if (card.getPreMonthMoney() == null || card.getPreMonthMoney() == 0) {
            return 1.0;
        }
        
        return Math.min(1.0, (double) userSpending / card.getPreMonthMoney());
    }
    
    /**
     * 추천 이유를 생성합니다.
     */
    private List<String> generateRecommendationReasons(BigDecimal cardBenefit, BigDecimal baseBenefit,
                                                       Map<String, BigDecimal> categoryBenefits) {
        List<String> reasons = new java.util.ArrayList<>();
        
        Long benefitDiff = cardBenefit.subtract(baseBenefit).longValue();
        reasons.add(String.format("현재 카드보다 연간 %,d원 더 많은 혜택을 받을 수 있습니다", benefitDiff));
        
        // 상위 2개 카테고리 언급
        categoryBenefits.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
            .limit(2)
            .forEach(entry -> {
                String category = entry.getKey();
                Long amount = entry.getValue().longValue();
                if (amount > 0) {
                    reasons.add(String.format("%s에서 월 %,d원의 혜택을 받을 수 있습니다", category, amount));
                }
            });
        
        return reasons;
    }
    
    /**
     * 주요 혜택 카테고리를 추출합니다.
     */
    private List<String> extractMainBenefitCategories(Map<String, BigDecimal> categoryBenefits, int limit) {
        return categoryBenefits.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public KbCardRecommendationResponseDTO recommendKbCards() {
        log.info("KB국민카드 추천 카드 목록 조회 시작");
        
        Long userId = null;
        try {
            userId = SecurityUtils.getCurrentUserId();
        } catch (Exception e) {
            log.debug("사용자 ID 조회 실패 (비로그인 상태일 수 있음): {}", e.getMessage());
        }
        
        try {
            List<CardProductVO> kbCards = cardRecommendationMapper.selectAvailableCardsByIssuer("KB국민카드");
            
            List<KbCardRecommendationResponseDTO.KbCardProductDTO> kbCardDTOs = kbCards.stream()
                .map(this::convertToKbCardDTO)
                .collect(Collectors.toList());
                
            // 로그인한 사용자인 경우 좋아요와 즐겨찾기 상태 설정
            if (userId != null) {
                final Long finalUserId = userId;
                kbCardDTOs.forEach(cardDto -> setKbCardLikeAndFavoriteStatus(finalUserId, cardDto));
            }
            
            String message = String.format("KB국민카드에서 발급 가능한 카드 %d개를 조회했습니다.", kbCardDTOs.size());
            
             log.info("KB국민카드 추천 완료: {} 개", kbCardDTOs.size());

            return KbCardRecommendationResponseDTO.builder()
                .kbCards(kbCardDTOs)
                .totalCount(kbCardDTOs.size())
                .message(message)
                .build();
                
        } catch (Exception e) {
            log.error("KB국민카드 추천 중 오류 발생", e);
            return KbCardRecommendationResponseDTO.builder()
                .kbCards(Collections.emptyList())
                .totalCount(0)
                .message("KB국민카드 추천 조회 중 오류가 발생했습니다.")
                .build();
        }
    }
    
    @Override
    public KbCardRecommendationResponseDTO recommendKbCardsWithPaging(int page, int size) {
        log.info("KB국민카드 페이징 추천 카드 목록 조회 시작 - page: {}, size: {}", page, size);
        
        Long userId = null;
        try {
            userId = SecurityUtils.getCurrentUserId();
        } catch (Exception e) {
            log.debug("사용자 ID 조회 실패 (비로그인 상태일 수 있음): {}", e.getMessage());
        }
        
        try {
            // 총 개수 조회
            int totalCount = cardRecommendationMapper.countAvailableCardsByIssuer("KB국민카드");
            
            // 페이징 계산
            int offset = page * size;
            boolean hasNext = (offset + size) < totalCount;
            
            // 페이징된 카드 목록 조회
            List<CardProductVO> kbCards = cardRecommendationMapper.selectAvailableCardsByIssuerWithPaging(
                "KB국민카드", offset, size);
            
            List<KbCardRecommendationResponseDTO.KbCardProductDTO> kbCardDTOs = kbCards.stream()
                .map(this::convertToKbCardDTO)
                .collect(Collectors.toList());
                
            // 로그인한 사용자인 경우 좋아요와 즐겨찾기 상태 설정
            if (userId != null) {
                final Long finalUserId = userId;
                kbCardDTOs.forEach(cardDto -> setKbCardLikeAndFavoriteStatus(finalUserId, cardDto));
            }
            
            String message = String.format("KB국민카드 %d페이지: %d개 카드 (전체 %d개)", 
                page + 1, kbCardDTOs.size(), totalCount);
            
            log.info("KB국민카드 페이징 추천 완료 - page: {}, size: {}, hasNext: {}", 
                page, kbCardDTOs.size(), hasNext);

            return KbCardRecommendationResponseDTO.builder()
                .kbCards(kbCardDTOs)
                .totalCount(totalCount)
                .message(message)
                .hasNext(hasNext)
                .currentPage(page)
                .pageSize(size)
                .build();
                
        } catch (Exception e) {
            log.error("KB국민카드 페이징 추천 중 오류 발생", e);
            return KbCardRecommendationResponseDTO.builder()
                .kbCards(Collections.emptyList())
                .totalCount(0)
                .message("KB국민카드 추천 조회 중 오류가 발생했습니다.")
                .hasNext(false)
                .currentPage(page)
                .pageSize(size)
                .build();
        }
    }
    
    /**
     * CardProductVO를 KbCardProductDTO로 변환합니다.
     */
    private KbCardRecommendationResponseDTO.KbCardProductDTO convertToKbCardDTO(CardProductVO card) {
        return KbCardRecommendationResponseDTO.KbCardProductDTO.builder()
            .cardProductId(card.getCardProductId())
            .name(card.getName())
            .type(card.getType())
            .annualFee(card.getAnnualFee())
            .preMonthMoney(card.getPreMonthMoney())
            .cardImageUrl(card.getCardImageUrl())
            .requestPcUrl(card.getRequestPcUrl())
            .requestMobileUrl(card.getRequestMobileUrl())
            .annualFeeDetail(card.getAnnualFeeDetail())
            .corpPrContainer(card.getCorpPrContainer())
            .corpTips(card.getCorpTips())
            .issuer(card.getIssuer())
            .build();
    }
    
    /**
     * KB 카드 DTO에 좋아요와 즐겨찾기 상태를 설정합니다.
     */
    private void setKbCardLikeAndFavoriteStatus(Long userId, KbCardRecommendationResponseDTO.KbCardProductDTO cardDto) {
        if (userId != null && cardDto.getCardProductId() != null) {
            try {
                // 좋아요 상태 조회
                boolean isLiked = cardDetailMapper.isLikedByUser(userId, cardDto.getCardProductId());
                int likeCount = cardDetailMapper.countLikesByProductId(cardDto.getCardProductId());
                cardDto.setLiked(isLiked);
                cardDto.setLikeCount(likeCount);
                
                // 즐겨찾기 상태 조회
                boolean isFavorited = userMapper.isCardFavoriteExists(userId, Long.valueOf(cardDto.getCardProductId()));
                cardDto.setFavorited(isFavorited);
            } catch (Exception e) {
                log.warn("KB 카드 {} 좋아요/즐겨찾기 상태 조회 실패: {}", cardDto.getCardProductId(), e.getMessage());
                // 기본값 설정
                cardDto.setLiked(false);
                cardDto.setLikeCount(0);
                cardDto.setFavorited(false);
            }
        } else {
            // 기본값 설정
            cardDto.setLiked(false);
            cardDto.setLikeCount(0);
            cardDto.setFavorited(false);
        }
    }
    
    /**
     * 카드 DTO에 좋아요와 즐겨찾기 상태를 설정합니다.
     */
    private void setLikeAndFavoriteStatus(Long userId, CardBenefitDTO cardDto) {
        if (userId != null && cardDto.getCardId() != null) {
            try {
                // 좋아요 상태 조회
                boolean isLiked = cardDetailMapper.isLikedByUser(userId, cardDto.getCardId());
                int likeCount = cardDetailMapper.countLikesByProductId(cardDto.getCardId());
                cardDto.setLiked(isLiked);
                cardDto.setLikeCount(likeCount);
                
                // 즐겨찾기 상태 조회
                boolean isStarred = userMapper.isCardFavoriteExists(userId, Long.valueOf(cardDto.getCardId()));
                cardDto.setStarred(isStarred);
            } catch (Exception e) {
                log.warn("카드 {} 좋아요/즐겨찾기 상태 조회 실패: {}", cardDto.getCardId(), e.getMessage());
                // 기본값 설정
                cardDto.setLiked(false);
                cardDto.setLikeCount(0);
                cardDto.setStarred(false);
            }
        } else {
            // 기본값 설정
            cardDto.setLiked(false);
            cardDto.setLikeCount(0);
            cardDto.setStarred(false);
        }
    }
    
    /**
     * 개선된 추천 점수를 계산합니다 (개인화 가중치 포함).
     */
    private Double calculateEnhancedRecommendationScore(
            BigDecimal cardBenefit, 
            BigDecimal baseBenefit,
            List<CardParsedBenefitVO> benefits, 
            List<CardTransactionSummaryVO> transactionSummaries,
            CardProductVO card,
            Map<String, Double> categoryWeights) {
        
        // 기본 점수 계산
        double baseScore = calculateRecommendationScore(cardBenefit, baseBenefit, benefits, transactionSummaries, card);
        
        // 개인화 보너스 점수 (최대 20점 추가)
        double personalizationBonus = calculatePersonalizationBonus(benefits, transactionSummaries, categoryWeights);
        
        // 카드 품질 점수 (발급사, 카드 타입 등 고려)
        double qualityBonus = calculateCardQualityScore(card);
        
        double totalScore = baseScore + personalizationBonus + qualityBonus;
        
        return Math.max(0, Math.min(100, totalScore));
    }
    
    /**
     * 개인화 보너스 점수를 계산합니다.
     */
    private double calculatePersonalizationBonus(
            List<CardParsedBenefitVO> benefits,
            List<CardTransactionSummaryVO> transactionSummaries,
            Map<String, Double> categoryWeights) {
        
        double bonus = 0.0;
        
        // 사용자의 주요 카테고리에 대한 혜택 매칭 보너스
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            String category = summary.getCategory();
            Double weight = categoryWeights.get(category);
            
            if (weight != null && weight > 1.0) { // 중요 카테고리
                boolean hasMatchingBenefit = benefits.stream()
                    .anyMatch(b -> CategoryMappingUtil.isCategoryMatch(b.getCategory(), category));
                
                if (hasMatchingBenefit) {
                    bonus += (weight - 1.0) * 10; // 가중치에 비례한 보너스
                }
            }
        }
        
        return Math.min(20.0, bonus);
    }
    
    /**
     * 카드 품질 점수를 계산합니다.
     */
    private double calculateCardQualityScore(CardProductVO card) {
        double score = 0.0;
        
        // 발급사별 점수 (주요 은행일수록 높은 점수)
        String issuer = card.getIssuer();
        if (issuer != null) {
            if (issuer.contains("KB") || issuer.contains("신한") || issuer.contains("우리") || 
                issuer.contains("하나") || issuer.contains("삼성")) {
                score += 3.0; // 주요 발급사
            } else if (issuer.contains("롯데") || issuer.contains("현대")) {
                score += 2.0; // 일반 발급사
            } else {
                score += 1.0; // 기타 발급사
            }
        }
        
        // 카드 타입별 점수
        String cardType = card.getType();
        if ("신용".equals(cardType)) {
            score += 2.0; // 신용카드가 일반적으로 혜택이 더 많음
        } else {
            score += 1.0; // 체크카드
        }
        
        return Math.min(5.0, score);
    }
    
    /**
     * 개선된 추천 이유를 생성합니다.
     */
    private List<String> generateEnhancedRecommendationReasons(
            BigDecimal cardBenefit, 
            BigDecimal baseBenefit,
            Map<String, BigDecimal> categoryBenefits,
            Map<String, Double> categoryWeights) {
        
        List<String> reasons = new ArrayList<>();
        
        // 총 혜택 개선 메시지
        Long benefitDiff = cardBenefit.subtract(baseBenefit).longValue();
        reasons.add(String.format("현재 카드보다 월 %,d원 더 많은 혜택을 받을 수 있습니다", benefitDiff));
        
        // 가중치가 높은 카테고리의 혜택 강조
        categoryBenefits.entrySet().stream()
            .filter(entry -> {
                Double weight = categoryWeights.get(entry.getKey());
                return weight != null && weight > 1.2; // 중요한 카테고리만
            })
            .sorted((a, b) -> {
                Double weightA = categoryWeights.get(a.getKey());
                Double weightB = categoryWeights.get(b.getKey());
                return weightB.compareTo(weightA);
            })
            .limit(2)
            .forEach(entry -> {
                String category = entry.getKey();
                BigDecimal benefit = entry.getValue();
                Double weight = categoryWeights.get(category);
                
                if (benefit.compareTo(BigDecimal.ZERO) > 0) {
                    reasons.add(String.format("자주 사용하시는 %s에서 월 %,d원의 혜택을 받을 수 있습니다 (사용 비중 %.1f%%)", 
                        category, benefit.intValue(), weight * 20)); // 가중치를 퍼센트로 변환
                }
            });
        
        // 연간 혜택 메시지
        Long yearlyBenefit = cardBenefit.multiply(BigDecimal.valueOf(12)).longValue();
        if (yearlyBenefit > 100000) {
            reasons.add(String.format("연간 총 %,d원의 혜택으로 카드 활용도가 매우 높습니다", yearlyBenefit));
        }
        
        return reasons.size() > 3 ? reasons.subList(0, 3) : reasons;
    }
    
    /**
     * 가중치를 고려한 주요 혜택 카테고리를 추출합니다.
     */
    private List<String> extractWeightedBenefitCategories(
            Map<String, BigDecimal> categoryBenefits, 
            Map<String, Double> categoryWeights, 
            int limit) {
        
        return categoryBenefits.entrySet().stream()
            .filter(entry -> entry.getValue().compareTo(BigDecimal.ZERO) > 0)
            .sorted((a, b) -> {
                // 혜택 금액과 가중치를 모두 고려한 점수로 정렬
                double scoreA = a.getValue().doubleValue() * categoryWeights.getOrDefault(a.getKey(), 1.0);
                double scoreB = b.getValue().doubleValue() * categoryWeights.getOrDefault(b.getKey(), 1.0);
                return Double.compare(scoreB, scoreA);
            })
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    /**
     * 저액 사용자(10만원 이하)를 위한 개인화된 카테고리 중심 추천
     * 혜택 금액보다는 카테고리 적합성과 사용 패턴을 중심으로 추천합니다.
     */
    private CardRecommendationResponseDTO recommendCardsForLowSpendingUser(
            Long userId, 
            CardProductVO baseCard, 
            List<CardTransactionSummaryVO> transactionSummaries,
            Long totalSpendAmount) {
        
        log.info("🎯 저액 사용자 개인화 추천 시작 - 사용액: {}원", totalSpendAmount);
        
        try {
            // 1. 사용자 프로파일 생성 (PersonalizedRecommendationEngine 활용)
            PersonalizedRecommendationUtil.UserProfile userProfile = 
                PersonalizedRecommendationUtil.buildUserProfile(transactionSummaries);
            
            log.info("👤 사용자 프로파일: 라이프스타일={}, 주요카테고리={}", 
                userProfile.getLifestyleType(), userProfile.getPrimaryCategories());
            
            // 2. 추천 카드 후보 조회 (연회비 무료 우선)
            List<CardProductVO> candidateCards = getAllCandidateCards(baseCard.getType(), baseCard.getCardProductId());
            
            // 3. 카테고리 매칭 기반 추천 점수 계산
            List<CategoryBasedRecommendation> recommendations = new ArrayList<>();
            
            for (CardProductVO card : candidateCards) {
                CategoryBasedRecommendation recommendation = 
                    analyzeCategoryFitness(card, userProfile, transactionSummaries);
                
                if (recommendation.getCategoryMatchScore() > 30.0) { // 최소 30점 이상
                    recommendations.add(recommendation);
                }
            }
            
            // 4. 추천 점수 순 정렬 및 상위 5개 선택
            List<CategoryBasedRecommendation> topRecommendations = recommendations.stream()
                .sorted((a, b) -> {
                    // 1차: 카테고리 매칭 점수, 2차: 연회비 무료 여부, 3차: 혜택 다양성
                    int scoreCompare = Double.compare(b.getCategoryMatchScore(), a.getCategoryMatchScore());
                    if (scoreCompare != 0) return scoreCompare;
                    
                    int feeCompare = Boolean.compare(a.isFreeAnnualFee(), b.isFreeAnnualFee());
                    if (feeCompare != 0) return feeCompare;
                    
                    return Integer.compare(b.getBenefitDiversity(), a.getBenefitDiversity());
                })
                .limit(5)
                .collect(Collectors.toList());
            
            // 5. CardBenefitDTO로 변환
            List<CardBenefitDTO> result = topRecommendations.stream()
                .map(rec -> convertToLowSpendingCardBenefitDTO(rec, userProfile))
                .collect(Collectors.toList());
            
            // 6. 각 추천 카드에 좋아요/즐겨찾기 상태 설정
            result.forEach(cardDto -> setLikeAndFavoriteStatus(userId, cardDto));
            
            // 7. 메시지 생성
            String message = generateLowSpendingUserMessage(userProfile, result.size(), totalSpendAmount);
            
            log.info("✅ 저액 사용자 개인화 추천 완료 - {} 개 카드", result.size());
            
            return CardRecommendationResponseDTO.builder()
                .totalSpendAmount(totalSpendAmount)
                .categoryStats(transactionSummaries.stream()
                    .map(this::convertToCategoryStatDTO)
                    .collect(Collectors.toList()))
                .recommendedCards(result)
                .message(message)
                .build();
                
        } catch (Exception e) {
            log.error("❌ 저액 사용자 추천 중 오류 발생: 사용자 {}", userId, e);
            return createEmptyRecommendationResponse(totalSpendAmount, transactionSummaries, 
                "추천 계산 중 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }
    
    /**
     * 모든 후보 카드를 조회합니다 (연회비 무료 카드 우선)
     */
    private List<CardProductVO> getAllCandidateCards(String cardType, Integer excludeCardId) {
        List<Integer> excludeIds = excludeCardId != null ? 
            Collections.singletonList(excludeCardId) : Collections.emptyList();
        
        // 같은 타입의 모든 카드 조회
        List<CardProductVO> allCards = cardRecommendationMapper.selectAvailableCardsByType(cardType, excludeIds);
        
        // 연회비 무료 카드를 앞에 배치
        return allCards.stream()
            .sorted((a, b) -> {
                boolean aFree = "무료".equals(a.getAnnualFee()) || a.getAnnualFee() == null;
                boolean bFree = "무료".equals(b.getAnnualFee()) || b.getAnnualFee() == null;
                return Boolean.compare(bFree, aFree); // 무료가 앞에 오도록
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 카테고리 적합성을 분석합니다.
     */
    private CategoryBasedRecommendation analyzeCategoryFitness(
            CardProductVO card, 
            PersonalizedRecommendationUtil.UserProfile userProfile,
            List<CardTransactionSummaryVO> transactionSummaries) {
        
        CategoryBasedRecommendation recommendation = new CategoryBasedRecommendation();
        recommendation.setCard(card);
        
        try {
            // 카드 혜택 정보 조회
            List<CardParsedBenefitVO> cardBenefits = 
                cardRecommendationMapper.selectCardBenefitsByCardId(card.getCardProductId());
            
            // 1. 카테고리 매칭 점수 계산 (0-100점)
            double categoryScore = calculateCategoryMatchingScore(cardBenefits, userProfile, transactionSummaries);
            recommendation.setCategoryMatchScore(categoryScore);
            
            // 2. 연회비 여부 확인
            boolean isFreeAnnualFee = "무료".equals(card.getAnnualFee()) || card.getAnnualFee() == null;
            recommendation.setFreeAnnualFee(isFreeAnnualFee);
            
            // 3. 혜택 다양성 점수 (여러 카테고리에 혜택이 있는지)
            int diversityScore = calculateBenefitDiversity(cardBenefits);
            recommendation.setBenefitDiversity(diversityScore);
            
            // 4. 추천 이유 생성
            List<String> reasons = generateCategoryBasedReasons(card, cardBenefits, userProfile);
            recommendation.setRecommendationReasons(reasons);
            
            // 5. 주요 혜택 카테고리 추출
            List<String> mainCategories = extractCardMainCategories(cardBenefits, userProfile.getPrimaryCategories());
            recommendation.setMainBenefitCategories(mainCategories);
            
        } catch (Exception e) {
            log.warn("⚠️ 카드 {} 분석 중 오류: {}", card.getName(), e.getMessage());
            recommendation.setCategoryMatchScore(0.0);
        }
        
        return recommendation;
    }
    
    /**
     * 카테고리 매칭 점수를 계산합니다 (저액 사용자용)
     */
    private double calculateCategoryMatchingScore(
            List<CardParsedBenefitVO> cardBenefits,
            PersonalizedRecommendationUtil.UserProfile userProfile,
            List<CardTransactionSummaryVO> transactionSummaries) {
        
        double totalScore = 0.0;
        Map<String, Double> categoryPreferences = userProfile.getCategoryPreferences();
        
        // 사용자의 각 거래 카테고리에 대해 카드 혜택 매칭 확인
        for (CardTransactionSummaryVO summary : transactionSummaries) {
            String userCategory = summary.getCategory();
            Double preference = categoryPreferences.getOrDefault(userCategory, 0.0);
            
            // 해당 카테고리에 매칭되는 카드 혜택이 있는지 확인
            boolean hasMatchingBenefit = cardBenefits.stream()
                .filter(benefit -> !"유의사항".equals(benefit.getTitle()))
                .anyMatch(benefit -> CategoryMappingUtil.isCategoryMatch(benefit.getCategory(), userCategory));
            
            if (hasMatchingBenefit) {
                // 선호도에 비례한 점수 부여 (최대 100점)
                totalScore += preference * 100;
                
                // 혜택 품질 보너스 (혜택율이 좋은 경우)
                double qualityBonus = calculateCategoryBenefitQuality(cardBenefits, userCategory);
                totalScore += qualityBonus;
            }
        }
        
        // 추가 보너스: 모든가맹점, 선택형 혜택
        boolean hasUniversalBenefit = cardBenefits.stream()
            .anyMatch(b -> "모든가맹점".equals(b.getTitle()) || "선택형".equals(b.getTitle()));
        if (hasUniversalBenefit) {
            totalScore += 15.0; // 15점 보너스
        }
        
        return Math.min(100.0, totalScore);
    }
    
    /**
     * 특정 카테고리에 대한 혜택 품질을 평가합니다.
     */
    private double calculateCategoryBenefitQuality(List<CardParsedBenefitVO> cardBenefits, String category) {
        return cardBenefits.stream()
            .filter(benefit -> CategoryMappingUtil.isCategoryMatch(benefit.getCategory(), category))
            .mapToDouble(benefit -> {
                BigDecimal value = benefit.getValue();
                if (value == null) return 0.0;
                
                // 혜택율이 높을수록 높은 점수 (최대 10점)
                if (value.compareTo(BigDecimal.valueOf(5)) >= 0) return 10.0; // 5% 이상
                if (value.compareTo(BigDecimal.valueOf(3)) >= 0) return 7.0;  // 3% 이상
                if (value.compareTo(BigDecimal.valueOf(1)) >= 0) return 5.0;  // 1% 이상
                return 2.0; // 그 외
            })
            .max()
            .orElse(0.0);
    }
    
    /**
     * 혜택 다양성 점수를 계산합니다.
     */
    private int calculateBenefitDiversity(List<CardParsedBenefitVO> cardBenefits) {
        Set<String> benefitCategories = cardBenefits.stream()
            .filter(benefit -> !"유의사항".equals(benefit.getTitle()))
            .map(benefit -> CategoryMappingUtil.mapBenefitToTransactionCategory(benefit.getCategory()))
            .collect(Collectors.toSet());
        
        return benefitCategories.size(); // 혜택이 있는 카테고리 수
    }
    
    /**
     * 카테고리 기반 추천 이유를 생성합니다.
     */
    private List<String> generateCategoryBasedReasons(
            CardProductVO card,
            List<CardParsedBenefitVO> cardBenefits,
            PersonalizedRecommendationUtil.UserProfile userProfile) {
        
        List<String> reasons = new ArrayList<>();
        
        // 주요 카테고리 매칭 이유
        List<String> primaryCategories = userProfile.getPrimaryCategories();
        for (String category : primaryCategories.stream().limit(2).collect(Collectors.toList())) {
            boolean hasMatchingBenefit = cardBenefits.stream()
                .anyMatch(benefit -> CategoryMappingUtil.isCategoryMatch(benefit.getCategory(), category));
            
            if (hasMatchingBenefit) {
                Double preference = userProfile.getCategoryPreferences().get(category);
                reasons.add(String.format("주로 사용하시는 %s 카테고리에서 혜택을 받을 수 있습니다 (사용 비중 %.1f%%)", 
                    category, preference * 100));
            }
        }
        
        // 연회비 무료 강조
        if ("무료".equals(card.getAnnualFee())) {
            reasons.add("연회비가 무료로 부담 없이 사용할 수 있습니다");
        }
        
        // 라이프스타일 적합성
        String lifestyleType = userProfile.getLifestyleType();
        if (lifestyleType != null) {
            String lifestyleMessage = getLifestyleMessage(lifestyleType);
            if (lifestyleMessage != null) {
                reasons.add(lifestyleMessage);
            }
        }
        
        // 기본 추천 이유
        if (reasons.isEmpty()) {
            reasons.add("귀하의 소비 패턴에 적합한 혜택을 제공하는 카드입니다");
        }
        
        return reasons.stream().limit(3).collect(Collectors.toList());
    }
    
    /**
     * 라이프스타일별 메시지를 반환합니다.
     */
    private String getLifestyleMessage(String lifestyleType) {
        switch (lifestyleType) {
            case "절약_실용형":
                return "실용적인 소비 패턴에 맞는 생활밀착형 혜택을 제공합니다";
            case "외식_선호형":
                return "외식을 자주 하시는 분에게 적합한 음식점 할인 혜택이 있습니다";
            case "이동_활발형":
                return "이동이 많으신 분에게 유용한 교통비 절약 혜택을 제공합니다";
            case "문화_생활형":
                return "문화생활을 즐기시는 분에게 적합한 엔터테인먼트 혜택이 있습니다";
            default:
                return null;
        }
    }
    
    /**
     * 카드의 주요 혜택 카테고리를 추출합니다.
     */
    private List<String> extractCardMainCategories(List<CardParsedBenefitVO> cardBenefits, List<String> userPrimaryCategories) {
        Set<String> cardCategories = cardBenefits.stream()
            .filter(benefit -> !"유의사항".equals(benefit.getTitle()))
            .map(benefit -> CategoryMappingUtil.mapBenefitToTransactionCategory(benefit.getCategory()))
            .collect(Collectors.toSet());
        
        // 사용자 주요 카테고리 중 카드가 지원하는 카테고리 우선 반환
        List<String> matchedCategories = userPrimaryCategories.stream()
            .filter(cardCategories::contains)
            .collect(Collectors.toList());
        
        // 매칭된 카테고리가 부족하면 카드의 다른 카테고리로 보완
        if (matchedCategories.size() < 3) {
            cardCategories.stream()
                .filter(cat -> !matchedCategories.contains(cat))
                .limit(3 - matchedCategories.size())
                .forEach(matchedCategories::add);
        }
        
        return matchedCategories.stream().limit(3).collect(Collectors.toList());
    }
    
    /**
     * 저액 사용자용 CardBenefitDTO로 변환합니다.
     */
    private CardBenefitDTO convertToLowSpendingCardBenefitDTO(
            CategoryBasedRecommendation recommendation,
            PersonalizedRecommendationUtil.UserProfile userProfile) {
        
        CardProductVO card = recommendation.getCard();
        
        return CardBenefitDTO.builder()
            .cardId(card.getCardProductId())
            .cardName(card.getName())
            .cardType(card.getType())
            .issuer(card.getIssuer())
            .estimatedBenefit(0L) // 저액 사용자는 혜택 금액 대신 0으로 표시
            .annualFee(card.getAnnualFee())
            .preMonthMoney(card.getPreMonthMoney())
            .cardImageUrl(card.getCardImageUrl())
            .requestPcUrl(card.getRequestPcUrl())
            .requestMobileUrl(card.getRequestMobileUrl())
            // 카테고리 중심 추천 필드
            .recommendationScore(recommendation.getCategoryMatchScore())
            .expectedMonthlyBenefit(BigDecimal.ZERO) // 금액 표시 안 함
            .expectedYearlyBenefit(BigDecimal.ZERO)  // 금액 표시 안 함
            .netBenefit(BigDecimal.ZERO)            // 금액 표시 안 함
            .categoryBenefits(new HashMap<>())       // 카테고리별 혜택 금액 안 보여줌
            .recommendationReasons(recommendation.getRecommendationReasons())
            .conditionFulfillmentProbability(1.0)   // 저액 사용자는 조건 충족 용이
            .expectedAchievementRate(1.0)           // 저액 사용자는 달성률 높음
            .mainBenefitCategories(recommendation.getMainBenefitCategories())
            .build();
    }
    
    /**
     * 저액 사용자를 위한 메시지를 생성합니다.
     */
    private String generateLowSpendingUserMessage(
            PersonalizedRecommendationUtil.UserProfile userProfile, 
            int recommendationCount, 
            Long totalSpendAmount) {
        
        if (recommendationCount == 0) {
            return "현재 거래 패턴에 맞는 추천 카드를 찾지 못했습니다. 더 많은 거래 후 다시 확인해보세요.";
        }
        
        String lifestyleType = userProfile.getLifestyleType();
        List<String> primaryCategories = userProfile.getPrimaryCategories();
        
        StringBuilder message = new StringBuilder();
        message.append(String.format("월 사용액 %,d원 기준으로 ", totalSpendAmount));
        
        if (!primaryCategories.isEmpty()) {
            if (primaryCategories.size() == 1) {
                message.append(String.format("%s 카테고리에 특화된 ", primaryCategories.get(0)));
            } else {
                message.append(String.format("%s, %s 등 주요 카테고리에 유리한 ", 
                    primaryCategories.get(0), primaryCategories.get(1)));
            }
        }
        
        message.append(String.format("%d개 카드를 추천합니다. ", recommendationCount));
        
        // 라이프스타일별 조언 추가
        if ("절약_실용형".equals(lifestyleType)) {
            message.append("연회비 무료 카드로 시작하여 점진적으로 혜택을 늘려가세요.");
        } else if ("외식_선호형".equals(lifestyleType)) {
            message.append("음식점 할인 혜택이 있는 카드를 선택하시면 도움이 됩니다.");
        } else {
            message.append("소비 패턴에 맞는 카드를 선택하여 혜택을 극대화하세요.");
        }
        
        return message.toString();
    }
    
    /**
     * 빈 추천 응답을 생성합니다.
     */
    private CardRecommendationResponseDTO createEmptyRecommendationResponse(
            Long totalSpendAmount, 
            List<CardTransactionSummaryVO> transactionSummaries, 
            String message) {
        
        return CardRecommendationResponseDTO.builder()
            .totalSpendAmount(totalSpendAmount)
            .categoryStats(transactionSummaries.stream()
                .map(this::convertToCategoryStatDTO)
                .collect(Collectors.toList()))
            .recommendedCards(Collections.emptyList())
            .message(message)
            .build();
    }
    
    /**
     * 카테고리 기반 추천 결과를 담는 내부 클래스
     */
    private static class CategoryBasedRecommendation {
        private CardProductVO card;
        private double categoryMatchScore;
        private boolean freeAnnualFee;
        private int benefitDiversity;
        private List<String> recommendationReasons;
        private List<String> mainBenefitCategories;
        
        // Getters and Setters
        public CardProductVO getCard() { return card; }
        public void setCard(CardProductVO card) { this.card = card; }
        
        public double getCategoryMatchScore() { return categoryMatchScore; }
        public void setCategoryMatchScore(double categoryMatchScore) { this.categoryMatchScore = categoryMatchScore; }
        
        public boolean isFreeAnnualFee() { return freeAnnualFee; }
        public void setFreeAnnualFee(boolean freeAnnualFee) { this.freeAnnualFee = freeAnnualFee; }
        
        public int getBenefitDiversity() { return benefitDiversity; }
        public void setBenefitDiversity(int benefitDiversity) { this.benefitDiversity = benefitDiversity; }
        
        public List<String> getRecommendationReasons() { return recommendationReasons; }
        public void setRecommendationReasons(List<String> recommendationReasons) { this.recommendationReasons = recommendationReasons; }
        
        public List<String> getMainBenefitCategories() { return mainBenefitCategories; }
        public void setMainBenefitCategories(List<String> mainBenefitCategories) { this.mainBenefitCategories = mainBenefitCategories; }
    }
}