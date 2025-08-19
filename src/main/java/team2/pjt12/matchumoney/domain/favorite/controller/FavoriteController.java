package team2.pjt12.matchumoney.domain.favorite.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.favorite.domain.FavoriteVO;
import team2.pjt12.matchumoney.domain.favorite.dto.FavoriteProductResponseDTO;
import team2.pjt12.matchumoney.domain.favorite.service.FavoriteService;
import team2.pjt12.matchumoney.global.ProductType;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
@Api(tags = "Favorite API", description = "즐겨찾기 API")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @ApiOperation(
            value = "즐겨찾기 추가",
            notes = "지정한 상품을 즐겨찾기에 추가합니다."
    )
    @PostMapping("/{productId}")
    public SuccessResponse<?> addFavorite(
            @ApiParam(value = "상품 ID", example = "123", required = true)
            @PathVariable Long productId,
            @RequestParam ProductType productType
    ) {
        favoriteService.addFavorite(productId, productType);
        return SuccessResponse.ok(productType.name() + " 즐겨찾기 추가 완료");
    }

    @ApiOperation(
            value = "즐겨찾기 삭제",
            notes = "지정한 상품을 즐겨찾기에서 제거합니다."
    )
    @DeleteMapping("/{productId}")
    public SuccessResponse<?> deleteFavorite(
            @ApiParam(value = "상품 ID", example = "123", required = true)
            @PathVariable Long productId,
            @RequestParam ProductType productType
    ) {
        favoriteService.deleteFavorite(productId, productType);
        return SuccessResponse.ok(productType.name() + " 즐겨찾기 삭제 완료");
    }

    @ApiOperation(
            value = "즐겨찾기 목록 조회",
            notes = "현재 사용자의 즐겨찾기(카드/적금/예금)를 모두 조회합니다."
    )
    @GetMapping
    public FavoriteProductResponseDTO getFavorites() {
        return favoriteService.getFavorites();
    }
}
