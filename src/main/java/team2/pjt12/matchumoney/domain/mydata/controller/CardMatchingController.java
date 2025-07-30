package team2.pjt12.matchumoney.domain.mydata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.mydata.dto.res.CardMatchingResultDTO;
import team2.pjt12.matchumoney.domain.mydata.service.CardMatchingService;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardVO;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/card-matching")
@RequiredArgsConstructor
@Api(tags = "Card Matching API", description = "카드 매칭 관리 API")
public class CardMatchingController {
    
    private final CardMatchingService cardMatchingService;
    
    @PostMapping("/match-all")
    @ApiOperation(
        value = "전체 카드 매칭", 
        notes = "mydata_card_holdings 테이블의 모든 매칭되지 않은 카드를 cards_products와 매칭합니다. " +
                "관리자용 기능으로 전체 데이터의 매칭 상태를 일괄 정리할 때 사용합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "매칭 처리 완료"),
        @ApiResponse(code = 500, message = "매칭 처리 중 오류 발생")
    })
    public ResponseEntity<SuccessResponse<CardMatchingResultDTO>> matchAllCardHoldings() {
        
        CardMatchingResultDTO responseDTO = cardMatchingService.matchAllCardHoldings();
        
        return ResponseEntity.ok(new SuccessResponse<>(responseDTO, "전체 카드 매칭이 완료되었습니다."));
    }
    
    @PostMapping("/match-user/{userId}")
    @ApiOperation(
        value = "특정 사용자 카드 매칭", 
        notes = "특정 사용자의 매칭되지 않은 카드 보유 정보를 cards_products와 매칭합니다. " +
                "카드 등록 후 자동 매칭이 실패한 경우나 수동으로 재매칭할 때 사용합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "사용자 카드 매칭 완료"),
        @ApiResponse(code = 404, message = "사용자를 찾을 수 없음"),
        @ApiResponse(code = 500, message = "매칭 처리 중 오류 발생")
    })
    public ResponseEntity<SuccessResponse<CardMatchingResultDTO>> matchCardHoldingsByUserId(
            @ApiParam(value = "사용자 ID", required = true, example = "5")
            @PathVariable Long userId) {
        
        CardMatchingResultDTO responseDTO = cardMatchingService.matchCardHoldingsByUserId(userId);
        
        return ResponseEntity.ok(new SuccessResponse<>(responseDTO, 
                String.format("사용자 %d의 카드 매칭이 완료되었습니다.", userId)));
    }
    
    @GetMapping("/unmatched")
    @ApiOperation(
        value = "매칭되지 않은 카드 목록 조회", 
        notes = "card_id가 null인 카드 보유 정보를 조회합니다. " +
                "자동 매칭에 실패한 카드들을 확인하고 수동 매칭을 진행할 때 사용합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "매칭되지 않은 카드 목록 조회 성공")
    })
    public ResponseEntity<SuccessResponse<List<CardHoldingVO>>> getUnmatchedCardHoldings() {
        
        List<CardHoldingVO> unmatchedCards = cardMatchingService.getUnmatchedCardHoldings();
        
        return ResponseEntity.ok(new SuccessResponse<>(unmatchedCards, 
                String.format("매칭되지 않은 카드 %d건을 조회했습니다.", unmatchedCards.size())));
    }
    
    @PutMapping("/manual-match")
    @ApiOperation(
        value = "수동 카드 매칭", 
        notes = "특정 카드 보유 정보에 수동으로 card_id를 설정합니다. " +
                "자동 매칭이 실패한 경우 관리자가 직접 매칭할 때 사용합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "수동 매칭 성공"),
        @ApiResponse(code = 400, message = "매칭 실패 또는 잘못된 파라미터"),
        @ApiResponse(code = 404, message = "카드 보유 정보 또는 카드 상품을 찾을 수 없음")
    })
    public ResponseEntity<SuccessResponse<String>> manualMatchCardHolding(
            @ApiParam(value = "카드 보유 ID", required = true, example = "51")
            @RequestParam Long holdingId,
            @ApiParam(value = "매칭할 카드 ID", required = true, example = "146")
            @RequestParam Integer cardId) {
        
        boolean success = cardMatchingService.manualMatchCardHolding(holdingId, cardId);
        
        if (success) {
            return ResponseEntity.ok(new SuccessResponse<>("매칭 완료", 
                    String.format("카드 보유 정보 %d에 카드 ID %d가 매칭되었습니다.", holdingId, cardId)));
        } else {
            return ResponseEntity.badRequest().body(new SuccessResponse<>("매칭 실패", 
                    "카드 매칭에 실패했습니다. 카드 보유 정보 또는 카드 상품 ID를 확인해주세요."));
        }
    }
    
    @GetMapping("/test-match")
    @ApiOperation(
        value = "카드 매칭 테스트", 
        notes = "특정 카드명으로 매칭이 되는지 테스트합니다. " +
                "매칭 로직 검증이나 디버깅 목적으로 사용합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "매칭 테스트 완료"),
        @ApiResponse(code = 400, message = "매칭 테스트 중 오류 발생")
    })
    public ResponseEntity<SuccessResponse<Object>> testCardMatching(
            @ApiParam(value = "테스트할 카드명", required = true, example = "The Easy카드")
            @RequestParam String cardName) {
        
        try {
            Optional<CardVO> result = cardMatchingService.testCardMatching(cardName);
            
            if (result.isPresent()) {
                return ResponseEntity.ok(new SuccessResponse<>(result.get(), 
                        String.format("'%s' 카드가 매칭되었습니다. 매칭된 카드: %s (ID: %d)", 
                                cardName, result.get().getName(), result.get().getCardId())));
            } else {
                return ResponseEntity.ok(new SuccessResponse<>(null, 
                        String.format("'%s' 카드와 매칭되는 카드를 찾을 수 없습니다.", cardName)));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new SuccessResponse<>(null, 
                    "매칭 테스트 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @GetMapping("/debug/user/{userId}")
    @ApiOperation(
        value = "사용자 카드 정보 디버그", 
        notes = "특정 사용자의 모든 카드 보유 정보를 조회합니다. " +
                "디버깅 목적으로 사용되며, 매칭 상태를 상세히 확인할 수 있습니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "사용자 카드 정보 조회 성공"),
        @ApiResponse(code = 404, message = "사용자를 찾을 수 없음")
    })
    public ResponseEntity<SuccessResponse<List<CardHoldingVO>>> debugUserCards(
            @ApiParam(value = "디버그할 사용자 ID", required = true, example = "5")
            @PathVariable Long userId) {
        
        List<CardHoldingVO> userCards = cardMatchingService.getAllCardHoldingsByUserId(userId);
        
        return ResponseEntity.ok(new SuccessResponse<>(userCards, 
                String.format("사용자 %d의 카드 보유 정보 %d건을 조회했습니다.", userId, userCards.size())));
    }
}
