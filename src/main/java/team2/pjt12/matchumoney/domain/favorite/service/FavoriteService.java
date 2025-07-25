package team2.pjt12.matchumoney.domain.favorite.service;

import team2.pjt12.matchumoney.global.ProductType;

public interface FavoriteService {

    void addFavorite(Long targetId, ProductType productType);
}
