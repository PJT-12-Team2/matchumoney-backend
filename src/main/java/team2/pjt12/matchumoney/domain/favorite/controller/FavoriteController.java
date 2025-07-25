package team2.pjt12.matchumoney.domain.favorite.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.favorite.service.FavoriteService;
import team2.pjt12.matchumoney.global.ProductType;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{productId}")
    public SuccessResponse<?> addFavorite(
            @PathVariable Long productId,
            @RequestParam ProductType productType) {
        favoriteService.addFavorite(productId, productType);
        return SuccessResponse.ok(productType.name() + " 즐겨찾기 추가 완료");
    }
}
