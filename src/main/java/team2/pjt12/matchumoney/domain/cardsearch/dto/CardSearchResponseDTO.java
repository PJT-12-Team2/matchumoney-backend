package team2.pjt12.matchumoney.domain.cardsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "카드 검색 결과 DTO")
public class CardSearchResponseDTO {
    @ApiModelProperty(value = "카드 상품 ID", example = "10", position = 1)
    private Long id;

    @ApiModelProperty(value = "카드명", example = "신한 Deep Dream 카드", position = 2)
    private String name;

    @ApiModelProperty(
            value = "카드 종류 (신용/체크)",
            allowableValues = "신용, 체크",
            example = "신용",
            position = 3
    )
    private String type;

    @ApiModelProperty(
            value = "카드 이미지 URL",
            example = "https://cdn.example.com/cards/101.png",
            position = 4
    )
    private String imageUrl;

    @ApiModelProperty(value = "카드사", example = "신한카드", position = 5)
    private String issuer;

    @ApiModelProperty(value = "연회비", example = "[국내] 10,000원 / [해외] 15,000원", position = 6)
    private String annualFee;

    @ApiModelProperty(value = "전월 실적", example = "300000", position = 7)
    private Integer preMonthMoney;

    @ApiModelProperty(
            value = "주요 혜택 옵션 리스트",
            notes = "각 옵션에는 혜택명, 한도, 전월 실적 조건 등이 포함됩니다.",
            position = 8
    )
    private List<CardOptionDTO> options;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "true", position = 9)
    private Boolean isStarred;

    @ApiModelProperty(value = "좋아요 수", example = "42", position = 10)
    private Long likeCount;

    @ApiModelProperty(value = "사용자 좋아요 여부", example = "false", position = 11)
    private Boolean isLiked;
}
