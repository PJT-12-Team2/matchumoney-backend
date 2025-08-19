package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "KB국민카드 추천 응답 DTO")
public class KbCardRecommendationResponseDTO {
    @ApiModelProperty(value = "추천 KB카드 목록")
    private List<KbCardProductDTO> kbCards;

    @ApiModelProperty(value = "총 개수", example = "44")
    private int totalCount;

    @ApiModelProperty(value = "메시지", example = "KB국민카드 4페이지: 6개 카드 (전체 44개)")
    private String message;

    @ApiModelProperty(value = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;

    @ApiModelProperty(value = "현재 페이지", example = "3")
    private int currentPage;

    @ApiModelProperty(value = "페이지 크기", example = "6")
    private int pageSize;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(description = "KB카드 상품 정보")
    public static class KbCardProductDTO {
        @ApiModelProperty(value = "카드 상품 ID", example = "10")
        private Integer cardProductId;

        @ApiModelProperty(value = "카드명", example = "KB국민 톡톡카드")
        private String name;

        @ApiModelProperty(value = "카드 종류", example = "신용")
        private String type;

        @ApiModelProperty(value = "연회비", example = "국내전용 12000원 / 해외겸용 15000원")
        private String annualFee;

        @ApiModelProperty(value = "전월 실적 금액", example = "300000")
        private Long preMonthMoney;

        @ApiModelProperty(value = "카드 이미지 URL", example = "https://cdn.example.com/card/10.png")
        private String cardImageUrl;

        @ApiModelProperty(value = "PC 신청 링크", example = "https://kbcard.example.com/apply/10")
        private String requestPcUrl;

        @ApiModelProperty(value = "모바일 신청 링크", example = "https://m.kbcard.example.com/apply/10")
        private String requestMobileUrl;

        @ApiModelProperty(value = "연회비 상세", example = "국내전용 1.2만원, 해외겸용 1.5만원")
        private String annualFeeDetail;

        @ApiModelProperty(value = "카드사 PR 컨테이너", required = false)
        private String corpPrContainer;

        @ApiModelProperty(value = "고릴라 TIP", required = false)
        private String corpTips;

        @ApiModelProperty(value = "카드사명", example = "KB국민카드")
        private String issuer;

        @ApiModelProperty(value = "좋아요 여부", example = "false")
        @JsonProperty("is_liked")
        private Boolean liked;

        @ApiModelProperty(value = "좋아요 수", example = "7")
        @JsonProperty("like_count")
        private Integer likeCount;

        @ApiModelProperty(value = "즐겨찾기 여부", example = "true")
        @JsonProperty("is_favorited")
        private Boolean favorited;
    }
}