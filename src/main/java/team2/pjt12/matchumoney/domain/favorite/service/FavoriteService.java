package team2.pjt12.matchumoney.domain.favorite.service;

import team2.pjt12.matchumoney.domain.favorite.domain.FavoriteVO;
import team2.pjt12.matchumoney.domain.favorite.dto.FavoriteProductResponseDTO;
import team2.pjt12.matchumoney.global.ProductType;

import java.util.List;

public interface FavoriteService {

    void addFavorite(Long productId, ProductType productType);

    void deleteFavorite(Long productId, ProductType productType);

    FavoriteProductResponseDTO getFavorites();
}
