package team2.pjt12.matchumoney.domain.cardsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;

import java.util.List;

@Getter
@Setter
@ApiModel(description = "카드 무한스크롤 목록에 노출되는 최소 카드 정보 DTO")
public class CardListItemDTO {
    @ApiModelProperty(value = "카드 상품 ID", example = "10")
    private Long id;

    @ApiModelProperty(value = "카드명", example = "신한 Deep Dream 카드")
    private String name;

    @ApiModelProperty(value = "카드사", example = "신한카드")
    private String issuer;

    @ApiModelProperty(value = "전월 실적 금액", example = "300000")
    private Integer preMonthMoney;

    @ApiModelProperty(value = "연회비", example = "국내전용 12,000원 / 해외겸용 15,000원")
    private String annualFee;

    @ApiModelProperty(value = "카드 이미지 URL", example = "https://cdn.example.com/card/2835.png")
    private String imageUrl;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    private Boolean isStarred;

    @ApiModelProperty(value = "사용자 좋아요 여부", example = "true")
    private Boolean isLiked;

    @ApiModelProperty(value = "좋아요 수", example = "42")
    private Integer likeCount;

    @ApiModelProperty(value = "대표 혜택 옵션 목록")
    private List<CardOptionDTO> options;
}
