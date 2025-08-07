package team2.pjt12.matchumoney.domain.favorite.controller;

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
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{productId}")
    public SuccessResponse<?> addFavorite(
            @PathVariable Long productId,
            @RequestParam ProductType productType) {
        favoriteService.addFavorite(productId, productType);
        return SuccessResponse.ok(productType.name() + " 즐겨찾기 추가 완료");
    }

    @DeleteMapping("/{productId}")
    public SuccessResponse<?> deleteFavorite(
            @PathVariable Long productId,
            @RequestParam ProductType productType) {
        favoriteService.deleteFavorite(productId, productType);
        return SuccessResponse.ok(productType.name() + " 즐겨찾기 삭제 완료");
    }

    @GetMapping
    public FavoriteProductResponseDTO getFavorites() {
        return favoriteService.getFavorites();
    }
}
