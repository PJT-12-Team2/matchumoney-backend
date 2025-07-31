package team2.pjt12.matchumoney.domain.cardrecommendation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.*;
import team2.pjt12.matchumoney.domain.cardrecommendation.mapper.CardRecommendationMapper;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.UserCardRecommendationService;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.BenefitCalculationUtil;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.*;

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

    @Override
    public MyCardBenefitResponseDTO calculateSpecificCardBenefit(Long userId, Integer cardId) {
        log.info("사용자 {}의 특정 카드 {} 혜택 계산 시작", userId, cardId);

        // 분석 기간 설정 (최근 30일)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(ANALYSIS_PERIOD_DAYS);

        // 특정 카드의 카테고리별 거래 통계 조회
        List<CardTransactionSummaryVO> transactionSummaries = 
            cardRecommendationMapper.selectTransactionSummaryByUserAndCard(userId, cardId, startDate, endDate);

        if (transactionSummaries.isEmpty()) {
            log.warn("사용자 {}의 카드 {}에 대한 최근 {}일 거래 내역이 없습니다.", userId, cardId, ANALYSIS_PERIOD_DAYS);
            return MyCardBenefitResponseDTO.builder()
                .totalSpendAmount(0L)
                .categoryStats(Collections.emptyList())
                .ownedCardBenefits(Collections.emptyList())
                .build();
        }

        // 특정 카드의 총 거래액 조회
        Long totalSpendAmount = cardRecommendationMapper.selectTotalSpendByUserAndCard(userId, cardId, startDate, endDate);

        // 특정 카드 정보 조회
        CardProductVO card = cardRecommendationMapper.selectCardById(cardId);

        if (card == null) {
            log.warn("카드 {}를 찾을 수 없습니다.", cardId);
            return MyCardBenefitResponseDTO.builder()
                .totalSpendAmount(totalSpendAmount)
                .categoryStats(transactionSummaries.stream()
                    .map(this::convertToCategoryStatDTO)
                    .collect(Collectors.toList()))
                .ownedCardBenefits(Collections.emptyList())
                .build();
        }

        // 특정 카드 혜택 계산
        List<CardParsedBenefitVO> benefits = 
            cardRecommendationMapper.selectCardBenefitsByCardId(cardId);

        BigDecimal totalBenefit = BenefitCalculationUtil.calculateTotalBenefit(
            benefits, transactionSummaries, totalSpendAmount, card.getPreMonthMoney());

        List<CardBenefitDTO> cardBenefits = Collections.singletonList(
            CardBenefitDTO.builder()
                .cardId(card.getCardProductId())
                .cardName(card.getName())
                .cardType(card.getType())
                .issuer(card.getIssuer())
                .estimatedBenefit(totalBenefit.longValue())
                .annualFee(card.getAnnualFee())
                .preMonthMoney(card.getPreMonthMoney())
                .cardImageUrl(card.getCardImageUrl())
                .requestPcUrl(card.getRequestPcUrl())
                .requestMobileUrl(card.getRequestMobileUrl())
                .build()
        );

        log.info("사용자 {}의 카드 {} 혜택 계산 완료. 예상 혜택: {}원", userId, cardId, totalBenefit);

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

        // 특정 카드의 카테고리별 거래 통계 조회
        List<CardTransactionSummaryVO> transactionSummaries = 
            cardRecommendationMapper.selectTransactionSummaryByUserAndCard(userId, cardId, startDate, endDate);

        if (transactionSummaries.isEmpty()) {
            log.warn("사용자 {}의 카드 {}에 대한 최근 {}일 거래 내역이 없어 추천이 어렵습니다.", userId, cardId, ANALYSIS_PERIOD_DAYS);
            return CardRecommendationResponseDTO.builder()
                .totalSpendAmount(0L)
                .categoryStats(Collections.emptyList())
                .recommendedCards(Collections.emptyList())
                .message("해당 카드의 최근 " + ANALYSIS_PERIOD_DAYS + "일 거래 내역이 없어 추천이 어렵습니다.")
                .build();
        }

        // 특정 카드의 총 거래액 조회
        Long totalSpendAmount = cardRecommendationMapper.selectTotalSpendByUserAndCard(userId, cardId, startDate, endDate);

        // 기준 카드 정보 조회
        CardProductVO baseCard = cardRecommendationMapper.selectCardById(cardId);
        if (baseCard == null) {
            log.warn("기준 카드 {}를 찾을 수 없습니다.", cardId);
            return CardRecommendationResponseDTO.builder()
                .totalSpendAmount(totalSpendAmount)
                .categoryStats(transactionSummaries.stream()
                    .map(this::convertToCategoryStatDTO)
                    .collect(Collectors.toList()))
                .recommendedCards(Collections.emptyList())
                .message("기준 카드 정보를 찾을 수 없습니다.")
                .build();
        }

        // 기준 카드의 혜택 계산
        List<CardParsedBenefitVO> baseBenefits = 
            cardRecommendationMapper.selectCardBenefitsByCardId(cardId);
        BigDecimal baseCardBenefit = BenefitCalculationUtil.calculateTotalBenefit(
            baseBenefits, transactionSummaries, totalSpendAmount, baseCard.getPreMonthMoney());

        log.info("기준 카드 {} 혜택: {}원", cardId, baseCardBenefit);

        // 같은 타입의 카드들 조회 (기준 카드 제외)
        log.info("기준 카드 타입: {} - 같은 타입 카드만 추천합니다", baseCard.getType());
        List<CardProductVO> availableCards = cardRecommendationMapper
            .selectAvailableCardsByType(baseCard.getType(), Collections.singletonList(cardId));

        List<CardBenefitDTO> betterCards = new ArrayList<>();

        // 각 카드별 혜택 계산하여 기준 카드보다 나은 카드 찾기
        for (CardProductVO card : availableCards) {
            List<CardParsedBenefitVO> benefits = 
                cardRecommendationMapper.selectCardBenefitsByCardId(card.getCardProductId());

            BigDecimal cardBenefit = BenefitCalculationUtil.calculateTotalBenefit(
                benefits, transactionSummaries, totalSpendAmount, card.getPreMonthMoney());

            // 기준 카드보다 혜택이 더 큰 카드만 추천
            if (cardBenefit.compareTo(baseCardBenefit) > 0) {
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
                    .build());
            }
        }

        // 혜택 순으로 정렬하고 상위 5개만 선택
        List<CardBenefitDTO> topRecommendations = betterCards.stream()
            .sorted((a, b) -> b.getEstimatedBenefit().compareTo(a.getEstimatedBenefit()))
            .limit(5)
            .collect(Collectors.toList());

        String message;
        if (topRecommendations.isEmpty()) {
            message = String.format("현재 카드(%s, 예상혜택: %s원)가 해당 소비 패턴에 가장 적합합니다.", 
                baseCard.getName(), baseCardBenefit.toString());
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
                MyCardBenefitResponseDTO cardBenefit = calculateSpecificCardBenefit(userId, card.getCardProductId());
                myCardsBenefits.add(cardBenefit);
            } catch (Exception e) {
                log.warn("사용자 {}의 카드 {} 혜택 계산 중 오류 발생, 건너뜁니다: {}", 
                    userId, card.getCardProductId(), e.getMessage());
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
            List<UserCardRecommendationVO> savedRecommendations = 
                userCardRecommendationService.getAllSavedRecommendations(userId);

            // 카드별 거래 통계 조회 (최근 30일)
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(ANALYSIS_PERIOD_DAYS);
            
            List<CardTransactionSummaryVO> transactionSummaries = 
                cardRecommendationMapper.selectTransactionSummaryByUserAndCard(userId, cardId, startDate, endDate);
            
            Long totalSpendAmount = cardRecommendationMapper.selectTotalSpendByUserAndCard(userId, cardId, startDate, endDate);

            // UserCardRecommendationVO를 CardBenefitDTO로 변환
            List<CardBenefitDTO> recommendedCards = savedRecommendations.stream()
                .map(this::convertToCardBenefitDTO)
                .collect(Collectors.toList());

            String message = savedRecommendations.isEmpty() ? 
                "저장된 추천 데이터가 없습니다. 거래내역을 먼저 동기화해주세요." :
                String.format("저장된 추천 카드 %d개를 조회했습니다.", savedRecommendations.size());

            return CardRecommendationResponseDTO.builder()
                .totalSpendAmount(totalSpendAmount)
                .categoryStats(transactionSummaries.stream()
                    .map(this::convertToCategoryStatDTO)
                    .collect(Collectors.toList()))
                .recommendedCards(recommendedCards)
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
            .build();
    }
}