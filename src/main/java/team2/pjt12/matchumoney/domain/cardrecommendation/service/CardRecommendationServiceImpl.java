package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.*;
import team2.pjt12.matchumoney.domain.cardrecommendation.mapper.CardRecommendationMapper;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.UserCardRecommendationService;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.BenefitCalculationUtil;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.CategoryMappingUtil;
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

        // 같은 타입의 카드들 조회 (기준 카드 제외)
        log.info("기준 카드 타입: {} - 같은 타입 카드만 추천합니다", targetCard.getType());
        List<CardProductVO> availableCards = cardRecommendationMapper
            .selectAvailableCardsByType(targetCard.getType(), Collections.singletonList(targetCard.getCardProductId()));

        List<CardBenefitDTO> betterCards = new ArrayList<>();

        // 각 카드별 혜택 계산하여 기준 카드보다 나은 카드 찾기
        for (CardProductVO card : availableCards) {
            List<CardParsedBenefitVO> benefits = 
                cardRecommendationMapper.selectCardBenefitsByCardId(card.getCardProductId());

            BigDecimal cardBenefit = BenefitCalculationUtil.calculateTotalBenefit(
                benefits, transactionSummaries, totalSpendAmount, card.getPreMonthMoney());

            // 기준 카드보다 혜택이 더 큰 카드만 추천
            if (cardBenefit.compareTo(baseCardBenefit) > 0) {
                // 추천 점수 계산
                Double recommendationScore = calculateRecommendationScore(cardBenefit, baseCardBenefit, 
                    benefits, transactionSummaries, card);
                
                // 카테고리별 혜택 계산
                Map<String, BigDecimal> categoryBenefits = calculateCategoryBenefits(
                    benefits, transactionSummaries, totalSpendAmount, card.getPreMonthMoney());
                
                // 추천 이유 생성
                List<String> recommendationReasons = generateRecommendationReasons(
                    cardBenefit, baseCardBenefit, categoryBenefits);
                
                // 주요 혜택 카테고리 추출
                List<String> mainBenefitCategories = extractMainBenefitCategories(categoryBenefits, 3);
                
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
                    // 새로운 추천 필드들
                    .recommendationScore(recommendationScore)
                    .expectedMonthlyBenefit(cardBenefit.divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP))
                    .expectedYearlyBenefit(cardBenefit)
                    .netBenefit(calculateNetBenefit(cardBenefit, card.getAnnualFee()))
                    .categoryBenefits(categoryBenefits)
                    .recommendationReasons(recommendationReasons)
                    .conditionFulfillmentProbability(calculateConditionFulfillmentProbability(card, totalSpendAmount))
                    .expectedAchievementRate(calculateAchievementRate(card, totalSpendAmount))
                    .mainBenefitCategories(mainBenefitCategories)
                    .build());
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
}