package team2.pjt12.matchumoney.domain.cardrecommendation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CardRecommendationResponseDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.MyCardBenefitResponseDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.CardRecommendationService;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.UserCardRecommendationService;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.UserCardRecommendationVO;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/card-recommendation")
@RequiredArgsConstructor
public class CardRecommendationController {

    private final CardRecommendationService cardRecommendationService;
    private final UserCardRecommendationService userCardRecommendationService;

    /**
     * 사용자의 특정 카드 혜택을 계산합니다.
     * 최근 30일 해당 카드의 거래내역을 기반으로 예상 혜택을 계산합니다.
     * 
     * @param cardId 특정 카드 ID
     * @return 특정 카드 예상 혜택 정보
     */
    @GetMapping("/cards/{cardId}/benefits")
    public ResponseEntity<MyCardBenefitResponseDTO> getSpecificCardBenefits(@PathVariable Integer cardId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("사용자 {}의 특정 카드 {} 혜택 조회 요청", userId, cardId);
            
            MyCardBenefitResponseDTO response = cardRecommendationService.calculateSpecificCardBenefit(userId, cardId);
            
            log.info("사용자 {}의 특정 카드 {} 혜택 조회 완료", userId, cardId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("특정 카드 혜택 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 사용자의 특정 카드 소비 패턴을 기반으로 더 나은 카드를 추천합니다.
     * 해당 카드의 최근 30일 거래내역을 분석하여 더 많은 혜택을 제공하는 카드들을 최대 5개 추천합니다.
     * 
     * @param cardId 기준이 되는 특정 카드 ID
     * @return 추천 카드 목록
     */
    @GetMapping("/cards/{cardId}/recommendations")
    public ResponseEntity<CardRecommendationResponseDTO> getBetterCardRecommendations(@PathVariable Integer cardId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("사용자 {}의 카드 {} 기준 더 나은 카드 추천 요청", userId, cardId);
            
            CardRecommendationResponseDTO response = cardRecommendationService.recommendBetterCards(userId, cardId);
            
            log.info("사용자 {}의 카드 {} 기준 추천 완료", userId, cardId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("카드 추천 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 사용자의 저장된 모든 추천 카드를 조회합니다.
     * 
     * @return 저장된 추천 카드 목록
     */
    @GetMapping("/saved-recommendations")
    public ResponseEntity<List<UserCardRecommendationVO>> getSavedRecommendations() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("사용자 {}의 저장된 추천 카드 목록 조회 요청", userId);
            
            List<UserCardRecommendationVO> savedRecommendations = 
                userCardRecommendationService.getAllSavedRecommendations(userId);
            
            log.info("사용자 {}의 저장된 추천 카드 {} 개 조회 완료", userId, savedRecommendations.size());
            return ResponseEntity.ok(savedRecommendations);
            
        } catch (Exception e) {
            log.error("저장된 추천 카드 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 사용자의 특정 기준 카드에 대한 저장된 추천 카드를 삭제합니다.
     * 
     * @param cardId 기준 카드 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/cards/{cardId}/saved-recommendations")
    public ResponseEntity<Void> deleteSavedRecommendations(@PathVariable Integer cardId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("사용자 {}의 카드 {} 기준 저장된 추천 카드 삭제 요청", userId, cardId);
            
            userCardRecommendationService.deleteRecommendations(userId, cardId);
            
            log.info("사용자 {}의 카드 {} 기준 저장된 추천 카드 삭제 완료", userId, cardId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            log.error("저장된 추천 카드 삭제 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 사용자가 보유한 모든 카드의 혜택을 조회합니다.
     * 저장된 추천 데이터가 있으면 데이터베이스에서 조회하고, 없으면 실시간 계산합니다.
     * 
     * @return 보유 카드별 혜택 정보
     */
    @GetMapping("/my-cards/benefits")
    public ResponseEntity<List<MyCardBenefitResponseDTO>> getMyCardsBenefits() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("사용자 {}의 보유 카드 혜택 조회 요청", userId);
            
            List<MyCardBenefitResponseDTO> myCardsBenefits = 
                cardRecommendationService.getMyCardsBenefits(userId);
            
            log.info("사용자 {}의 보유 카드 {} 개 혜택 조회 완료", userId, myCardsBenefits.size());
            return ResponseEntity.ok(myCardsBenefits);
            
        } catch (Exception e) {
            log.error("보유 카드 혜택 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 특정 카드의 저장된 추천 데이터를 조회합니다.
     * 데이터베이스에 저장된 추천 결과를 반환합니다.
     * 
     * @param cardId 카드 ID
     * @return 저장된 추천 카드 목록
     */
    @GetMapping("/cards/{cardId}/saved-recommendations")
    public ResponseEntity<CardRecommendationResponseDTO> getSavedCardRecommendations(@PathVariable Integer cardId) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            log.info("사용자 {}의 카드 {} 저장된 추천 조회 요청", userId, cardId);
            
            CardRecommendationResponseDTO savedRecommendations = 
                cardRecommendationService.getSavedRecommendations(userId, cardId);
            
            log.info("사용자 {}의 카드 {} 저장된 추천 조회 완료", userId, cardId);
            return ResponseEntity.ok(savedRecommendations);
            
        } catch (Exception e) {
            log.error("저장된 추천 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}