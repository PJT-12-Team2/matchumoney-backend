package team2.pjt12.matchumoney.domain.compare.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team2.pjt12.matchumoney.domain.compare.dto.CompareProductsResponseDTO;
import team2.pjt12.matchumoney.domain.compare.dto.SearchProductResponseDTO;

import java.util.List;

@Api(tags = "Compare API", description = "상품 비교 관련 API")
@RequestMapping("/api/compare")
public interface CompareApi {

    @ApiOperation(value = "타입과 ID 리스트로 상품 정보 조회", notes = "해당 타입과 ID 리스트에 해당하는 상품 정보를 조회합니다.")
    @GetMapping("/{type}")
    ResponseEntity<CompareProductsResponseDTO> getProductInfoList(
            @ApiParam(value = "상품 타입", example = "saving") @PathVariable("type") String type,
            @ApiParam(value = "적금 상품 ID 리스트") @RequestParam("ids") List<Long> ids
    );

    @ApiOperation(value = "해당 타입 상품 조회", notes = "해당하는 타입의 상품 리스트를 조회합니다.")
    @GetMapping("/{type}/all")
    ResponseEntity<List<SearchProductResponseDTO>> getProductList(
            @ApiParam(value = "상품 타입", example = "saving") @PathVariable("type") String type
    );
}
