package team2.pjt12.matchumoney.domain.favorite.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteVO {
    private Long favoriteId;
    private Long userId;
    private Long depositProductId;
    private Long savingProductId;
    private Long cardProductId;

    @Builder
    public FavoriteVO(Long userId,
                      Long depositProductId,
                      Long savingProductId,
                      Long cardProductId) {
        this.userId = userId;
        this.depositProductId = depositProductId;
        this.savingProductId = savingProductId;
        this.cardProductId = cardProductId;
    }
}
