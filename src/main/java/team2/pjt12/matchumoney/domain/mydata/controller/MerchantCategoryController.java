package team2.pjt12.matchumoney.domain.mydata.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.mydata.service.MerchantCategoryService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import java.util.*;

@RestController
@RequestMapping("/api/merchant-category")
@RequiredArgsConstructor
@Api(tags = "Card Merchant Category API", description = "카드 내역 가맹점 소비 분야 분류 API")
public class MerchantCategoryController {

    private final MerchantCategoryService merchantCategoryService;

    @GetMapping("/classify")
    @ApiOperation(
            value = "가맹점명 분류",
            notes = "특정 가맹점명을 입력하여 어떤 소비 분야로 분류되는지 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "분류 완료"),
            @ApiResponse(code = 400, message = "잘못된 요청 파라미터")
    })
    public ResponseEntity<SuccessResponse<Map<String, Object>>> classifyMerchant(
            @ApiParam(value = "분류할 가맹점명", required = true, example = "스타벅스 강남점")
            @RequestParam String merchantName) {

        Map<String, Object> result = merchantCategoryService.categorizeWithDetails(merchantName);

        return ResponseEntity.ok(new SuccessResponse<>(result,
                String.format("가맹점 '%s'이(가) '%s' 분야로 분류되었습니다.",
                        merchantName, result.get("category"))));
    }

    @PostMapping("/classify-batch")
    @ApiOperation(
            value = "가맹점명 일괄 분류",
            notes = "여러 가맹점명을 한 번에 분류하여 결과를 확인합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "일괄 분류 완료"),
            @ApiResponse(code = 400, message = "잘못된 요청 데이터")
    })
    public ResponseEntity<SuccessResponse<List<Map<String, Object>>>> classifyMerchantsBatch(
            @ApiParam(value = "분류할 가맹점명 목록", required = true)
            @RequestBody List<String> merchantNames) {

        List<Map<String, Object>> results = new ArrayList<>();

        for (String merchantName : merchantNames) {
            Map<String, Object> result = new HashMap<>();
            result.put("merchantName", merchantName);
            result.put("category", merchantCategoryService.categorizeByMerchantName(merchantName));
            results.add(result);
        }

        return ResponseEntity.ok(new SuccessResponse<>(results,
                String.format("총 %d개의 가맹점명이 분류되었습니다.", results.size())));
    }

    @GetMapping("/categories")
    @ApiOperation(
            value = "지원되는 소비 분야 목록 조회",
            notes = "시스템에서 지원하는 모든 소비 분야 카테고리를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "카테고리 목록 조회 성공")
    })
    public ResponseEntity<SuccessResponse<Set<String>>> getSupportedCategories() {

        Set<String> categories = merchantCategoryService.getSupportedCategories();

        return ResponseEntity.ok(new SuccessResponse<>(categories,
                String.format("총 %d개의 소비 분야 카테고리를 지원합니다.", categories.size())));
    }

    @GetMapping("/categories/{category}/keywords")
    @ApiOperation(
            value = "특정 카테고리의 키워드 목록 조회",
            notes = "특정 소비 분야 카테고리에 속하는 키워드들을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "키워드 목록 조회 성공"),
            @ApiResponse(code = 404, message = "존재하지 않는 카테고리")
    })
    public ResponseEntity<SuccessResponse<List<String>>> getCategoryKeywords(
            @ApiParam(value = "카테고리명", required = true, example = "카페")
            @PathVariable String category) {

        List<String> keywords = merchantCategoryService.getCategoryKeywords(category);

        if (keywords.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new SuccessResponse<>(keywords,
                String.format("'%s' 카테고리에는 %d개의 키워드가 등록되어 있습니다.",
                        category, keywords.size())));
    }
}
