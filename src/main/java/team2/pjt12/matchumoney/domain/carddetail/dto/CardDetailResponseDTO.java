package team2.pjt12.matchumoney.domain.carddetail.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel(description = "카드 상세 응답 DTO")
public class CardDetailResponseDTO {
    @ApiModelProperty(value = "카드 상품 ID", example = "10")
    private int cardProductId;

    @ApiModelProperty(value = "카드명", example = "신한 Deep Dream 카드")
    private String name;

    @ApiModelProperty(value = "카드 종류", example = "신용카드")
    private String type;

    @ApiModelProperty(value = "발급 가능 여부", example = "true")
    private boolean available;

    @ApiModelProperty(value = "카드사 ID", example = "7")
    private int issuerId;

    @ApiModelProperty(value = "연회비", example = "국내전용 12,000원 / 해외겸용 15,000원")
    private String annualFee;

    @ApiModelProperty(value = "전월 실적 금액", example = "300000")
    private Integer preMonthMoney;

    @ApiModelProperty(value = "온라인 전용 여부", example = "false")
    private Boolean onlineOnly;

    @ApiModelProperty(value = "카드 이미지 URL", example = "https://cdn.example.com/card/10.png")
    private String cardImageUrl;

    @ApiModelProperty(value = "PC 신청 URL", example = "https://card.example.com/apply/10")
    private String requestPcUrl;

    @ApiModelProperty(value = "모바일 신청 URL", example = "https://m.card.example.com/apply/10")
    private String requestMobileUrl;

    @ApiModelProperty(value = "연회비 상세 설명", example = "국내전용 12,000원, 해외겸용 15,000원(부가세 포함)")
    private String annualFeeDetail;

    @ApiModelProperty(value = "카드사 PR 컨테이너", example = "온라인 신규회원 연회비 100% 캐시백")
    private String corpPrContainer;

    @ApiModelProperty(value = "카드사 PR 상세", required = false)
    private String corpPrDetail;

    @ApiModelProperty(value = "고릴라 TIP", required = false)
    private String corpTips;

    @ApiModelProperty(value = "사용자 페르소나 ID", example = "2")
    private long personaId;

    @ApiModelProperty(value = "카드사명", example = "신한카드")
    private String issuer;

    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "현재 사용자 좋아요 여부", example = "true")
    private boolean liked;

    @ApiModelProperty(value = "좋아요 개수", example = "12")
    private int likeCount;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    private boolean starred;

    @ApiModelProperty(value = "혜택 옵션 목록")
    private List<CardOptionDTO> options;
}
