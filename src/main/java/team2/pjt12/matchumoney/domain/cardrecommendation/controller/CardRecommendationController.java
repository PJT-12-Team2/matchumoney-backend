package team2.pjt12.matchumoney.domain.cardrecommendation.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CardRecommendationResponseDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.dto.MyCardBenefitResponseDTO;
//import team2.pjt12.matchumoney.domain.cardrecommendation.dto.CategoryStatDTO;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.CardRecommendationService;
import team2.pjt12.matchumoney.domain.cardrecommendation.service.UserCardRecommendationService;
import team2.pjt12.matchumoney.domain.cardrecommendation.vo.UserCardRecommendationVO;
import team2.pjt12.matchumoney.global.util.SecurityUtils;
import team2.pjt12.matchumoney.domain.cardrecommendation.util.CategoryMappingUtil;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/card-recommendation")
@RequiredArgsConstructor
@Api(tags = "Card Recommendation API", description = "사용자 맞춤 카드 추천 API - 23개 표준 카테고리 기반")
public class CardRecommendationController {

    private final CardRecommendationService cardRecommendationService;
    private final UserCardRecommendationService userCardRecommendationService;

    @GetMapping("/cards/{cardId}/benefits")
    @ApiOperation(
            value = "특정 카드 혜택 계산",
            notes = "최근 30일 거래내역을 기반으로 특정 카드의 예상 혜택을 계산합니다. " +
                    "MerchantCategoryService를 통해 23개 표준 카테고리로 분류됩니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "카드 혜택 계산 성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "카드 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<MyCardBenefitResponseDTO> getSpecificCardBenefits(
            @ApiParam(value = "카드 ID", required = true, example = "1")
            @PathVariable Integer cardId) {
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

    @GetMapping("/cards/{cardId}/recommendations")
    @ApiOperation(
            value = "더 나은 카드 추천",
            notes = "특정 카드의 최근 30일 거래내역을 분석하여 더 많은 혜택을 제공하는 카드들을 최대 5개 추천합니다. " +
                    "AI 기반 점수 시스템, 카테고리별 혜택, 예상 순 혜택 등이 포함됩니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "카드 추천 성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "카드 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<CardRecommendationResponseDTO> getBetterCardRecommendations(
            @ApiParam(value = "기준 카드 ID", required = true, example = "1")
            @PathVariable Integer cardId) {
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

    @GetMapping("/saved-recommendations")
    @ApiOperation(
            value = "저장된 추천 카드 목록 조회",
            notes = "사용자의 저장된 모든 추천 카드를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "저장된 추천 카드 조회 성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
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

    @DeleteMapping("/cards/{cardId}/saved-recommendations")
    @ApiOperation(
            value = "저장된 추천 카드 삭제",
            notes = "사용자의 특정 기준 카드에 대한 저장된 추천 카드를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "저장된 추천 카드 삭제 성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "카드 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<Void> deleteSavedRecommendations(
            @ApiParam(value = "기준 카드 ID", required = true, example = "1")
            @PathVariable Integer cardId) {
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

    @GetMapping("/my-cards/benefits")
    @ApiOperation(
            value = "보유 카드 혜택 조회",
            notes = "보유한 모든 카드의 예상 혜택을 조회합니다. " +
                    "저장된 추천 데이터가 있으면 데이터베이스에서 조회하고, 없으면 실시간 계산합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "보유 카드 혜택 조회 성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
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

    @GetMapping("/cards/{cardId}/saved-recommendations")
    @ApiOperation(
            value = "특정 카드의 저장된 추천 조회",
            notes = "데이터베이스에 저장된 특정 카드의 추천 결과를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "저장된 추천 조회 성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 404, message = "카드 정보를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<CardRecommendationResponseDTO> getSavedCardRecommendations(
            @ApiParam(value = "카드 ID", required = true, example = "1")
            @PathVariable Integer cardId) {
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

    @GetMapping("/categories")
    @ApiOperation(
            value = "지원 카테고리 목록",
            notes = "지원되는 모든 표준 카테고리 목록을 반환합니다. 23개 표준 카테고리 체계를 확인할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "카테고리 목록 조회 성공"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<java.util.Set<String>> getSupportedCategories() {
        try {
            log.info("지원 카테고리 목록 요청");
            
            java.util.Set<String> supportedCategories = CategoryMappingUtil.getSupportedCategories();
            
            log.info("지원 카테고리 {} 개 조회 완료", supportedCategories.size());
            return ResponseEntity.ok(supportedCategories);
            
        } catch (Exception e) {
            log.error("지원 카테고리 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/categories/{transactionCategory}/benefit-mappings")
    @ApiOperation(
            value = "카테고리 매핑 조회",
            notes = "특정 거래내역 카테고리에 매핑되는 모든 혜택 카테고리들을 반환합니다. " +
                    "카드 혜택과 거래내역 사이의 매핑 관계를 확인할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "카테고리 매핑 조회 성공"),
            @ApiResponse(code = 404, message = "카테고리를 찾을 수 없음"),
            @ApiResponse(code = 500, message = "서버 오류")
    })
    public ResponseEntity<List<String>> getBenefitCategoriesForTransaction(
            @ApiParam(value = "거래내역 카테고리", required = true, example = "음식점")
            @PathVariable String transactionCategory) {
        try {
            log.info("거래내역 카테고리 '{}'에 매핑되는 혜택 카테고리 조회 요청", transactionCategory);
            
            List<String> benefitCategories = CategoryMappingUtil.getBenefitCategoriesForTransaction(transactionCategory);
            
            log.info("거래내역 카테고리 '{}'에 매핑되는 혜택 카테고리 {} 개 조회 완료", 
                transactionCategory, benefitCategories.size());
            return ResponseEntity.ok(benefitCategories);
            
        } catch (Exception e) {
            log.error("카테고리 매핑 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}