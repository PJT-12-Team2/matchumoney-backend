package team2.pjt12.matchumoney.domain.mydata.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.mydata.dto.KbCardApiRequestDTO;
import team2.pjt12.matchumoney.domain.mydata.dto.KbCardTransactionRequestDTO;
import team2.pjt12.matchumoney.domain.mydata.dto.res.CardInfoResponseDTO;
import team2.pjt12.matchumoney.domain.mydata.dto.res.CardTransactionResponseDTO;
import team2.pjt12.matchumoney.domain.mydata.service.KbCardService;
import team2.pjt12.matchumoney.domain.mydata.util.CardDTOConverter;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardTransactionVO;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
@Api(tags = "KB Card MyData API", description = "KB카드 마이데이터 연동 API")
public class KbCardController {

    private final KbCardService kbCardService;

    @PostMapping("/cards")
    @ApiOperation(
        value = "사용자 카드 정보 동기화", 
        notes = "KB카드 마이데이터 API를 통해 사용자의 카드 정보를 조회하고 저장합니다. " +
                "기존 카드 정보는 삭제되고 새로운 정보로 업데이트됩니다. " +
                "카드 정보 저장 후 자동으로 cards_products 테이블과 매칭을 시도합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "카드 정보 동기화 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청 데이터"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 500, message = "MyData API 호출 실패 또는 서버 오류")
    })
    public ResponseEntity<SuccessResponse<List<CardInfoResponseDTO>>> syncKbCards(
            @ApiParam(value = "KB카드 API 요청 정보", required = true)
            @Valid @RequestBody KbCardApiRequestDTO request) throws Exception {
        
        List<CardHoldingVO> cardHoldingVOList = kbCardService.syncAndSaveCards(
            request.getUserId(), 
            request.getCardId(), 
            request.getCardPw()
        );
        
        List<CardInfoResponseDTO> responseDTOList = CardDTOConverter.toCardInfoResponseDTOList(cardHoldingVOList);
        
        return ResponseEntity.ok(new SuccessResponse<>(responseDTOList, 
            String.format("총 %d개의 카드 정보가 동기화되었습니다.", responseDTOList.size())));
    }

    @GetMapping("/{userId}/cards")
    @ApiOperation(
        value = "사용자 카드 목록 조회", 
        notes = "저장된 사용자의 카드 보유 정보를 조회합니다. " +
                "카드 정보에는 카드명, 카드번호, 유효기간, 상태 등이 포함됩니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "카드 목록 조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 404, message = "사용자 또는 카드 정보를 찾을 수 없음")
    })
    public ResponseEntity<SuccessResponse<List<CardInfoResponseDTO>>> getUserCards(
            @ApiParam(value = "사용자 ID", required = true, example = "1")
            @PathVariable Long userId) {
        
        List<CardHoldingVO> cardHoldingVOList = kbCardService.getCards(userId);
        List<CardInfoResponseDTO> responseDTOList = CardDTOConverter.toCardInfoResponseDTOList(cardHoldingVOList);
        
        return ResponseEntity.ok(new SuccessResponse<>(responseDTOList, 
            String.format("사용자 %d의 카드 %d개를 조회했습니다.", userId, responseDTOList.size())));
    }

    @PostMapping("/cards/{holdingId}/transactions")
    @ApiOperation(
        value = "카드 거래 내역 동기화", 
        notes = "특정 카드의 거래 내역을 KB카드 마이데이터 API를 통해 조회하고 저장합니다. " +
                "기존 거래 내역은 삭제되고 새로운 내역으로 업데이트됩니다. " +
                "조회 기간은 최대 1년까지 가능합니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "거래 내역 동기화 성공"),
        @ApiResponse(code = 400, message = "잘못된 요청 데이터 또는 날짜 범위"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 404, message = "카드 정보를 찾을 수 없음"),
        @ApiResponse(code = 500, message = "MyData API 호출 실패 또는 서버 오류")
    })
    public ResponseEntity<SuccessResponse<List<CardTransactionResponseDTO>>> syncCardTransactions(
            @ApiParam(value = "카드 보유 ID", required = true, example = "1")
            @PathVariable Long holdingId,
            @ApiParam(value = "카드 거래 내역 조회 요청 정보", required = true)
            @Valid @RequestBody KbCardTransactionRequestDTO request) throws Exception {
        
        List<CardTransactionVO> transactionVOList = kbCardService.syncAndSaveCardTransactions(
                request.getUserId(), 
                holdingId, 
                request.getCardNo(), 
                request.getCardPw2(),
                request.getBirthDate(), 
                request.getStartDate(), 
                request.getEndDate()
        );
        
        List<CardTransactionResponseDTO> responseDTOList = CardDTOConverter.toCardTransactionResponseDTOList(transactionVOList);
        
        return ResponseEntity.ok(new SuccessResponse<>(responseDTOList, 
            String.format("총 %d건의 거래 내역이 동기화되었습니다.", responseDTOList.size())));
    }

    @GetMapping("/cards/{holdingId}/transactions")
    @ApiOperation(
        value = "저장된 카드 거래 내역 조회",
        notes = "이미 저장된 특정 카드의 거래 내역을 조회합니다. " +
                "MyData API를 호출하지 않고 데이터베이스에서 조회합니다. " +
                "각 거래 내역에는 자동 분류된 소비 분야(merchantCategory)가 포함됩니다."
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "거래 내역 조회 성공"),
        @ApiResponse(code = 401, message = "인증 실패"),
        @ApiResponse(code = 404, message = "카드 또는 거래 내역을 찾을 수 없음")
    })
    public ResponseEntity<SuccessResponse<List<CardTransactionResponseDTO>>> getStoredCardTransactions(
            @ApiParam(value = "카드 보유 ID", required = true, example = "1")
            @PathVariable Long holdingId,
            @ApiParam(value = "사용자 ID", required = true, example = "1")
            @RequestParam Long userId) {

        List<CardTransactionVO> transactionVOList = kbCardService.getCardTransactions(userId, holdingId);
        List<CardTransactionResponseDTO> responseDTOList = CardDTOConverter.toCardTransactionResponseDTOList(transactionVOList);

        // 카테고리별 통계 추가
        Map<String, Long> categoryStats = kbCardService.getCategoryStatistics(transactionVOList);

        String message = String.format("카드 %d의 거래 내역 %d건을 조회했습니다. 카테고리별 분포: %s",
                holdingId, responseDTOList.size(), categoryStats);

        return ResponseEntity.ok(new SuccessResponse<>(responseDTOList, message));
    }
}
