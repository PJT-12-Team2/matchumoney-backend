package team2.pjt12.matchumoney.domain.favorite.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FavoriteVO {
    private Long favoriteId;
    private Long userId;
    private Long depositProductId;
    private Long savingProductId;
    private Long cardProductId;
    private LocalDateTime createdTime;
    private LocalDateTime lastModifiedTime;

    @Builder
    public FavoriteVO(Long userId,
                      Long depositProductId,
                      Long savingProductId,
                      Long cardProductId,
                      LocalDateTime createdTime,
                      LocalDateTime lastModifiedTime) {
        this.userId = userId;
        this.depositProductId = depositProductId;
        this.savingProductId = savingProductId;
        this.cardProductId = cardProductId;
        this.createdTime = createdTime;
        this.lastModifiedTime = lastModifiedTime;
    }
}
