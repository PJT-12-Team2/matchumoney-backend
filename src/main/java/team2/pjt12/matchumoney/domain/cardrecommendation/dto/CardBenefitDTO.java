package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "추천 카드 혜택 상세 DTO")
public class CardBenefitDTO {
    @ApiModelProperty(value = "카드 ID", example = "10")
    private Integer cardId;

    @ApiModelProperty(value = "카드명", example = "신한 Deep Dream 카드")
    private String cardName;

    @ApiModelProperty(value = "카드 종류", allowableValues = "신용, 체크", example = "신용")
    private String cardType;

    @ApiModelProperty(value = "발급사", example = "신한카드")
    private String issuer;

    @ApiModelProperty(value = "예상 혜택 금액(원)", example = "45000")
    private Long estimatedBenefit;

    @ApiModelProperty(value = "연회비", example = "국내 10,000원 / 해외 15,000원")
    private String annualFee;

    @ApiModelProperty(value = "전월 실적 조건(원)", example = "300000")
    private Long preMonthMoney;

    @ApiModelProperty(value = "카드 이미지 URL", example = "https://cdn.example.com/cards/101.png")
    private String cardImageUrl;

    @ApiModelProperty(value = "PC 신청 링크", example = "https://issuer.example.com/apply/pc/101")
    private String requestPcUrl;

    @ApiModelProperty(value = "모바일 신청 링크", example = "https://issuer.example.com/apply/mo/101")
    private String requestMobileUrl;
    
    // 추천 시스템을 위한 추가 필드
    @ApiModelProperty(value = "추천 점수(0~100)", example = "87.5")
    private Double recommendationScore;

    @ApiModelProperty(value = "예상 월 혜택 금액", example = "12000")
    private BigDecimal expectedMonthlyBenefit;

    @ApiModelProperty(value = "예상 연 혜택 금액", example = "144000")
    private BigDecimal expectedYearlyBenefit;

    @ApiModelProperty(value = "순 혜택(연 혜택 - 연회비)", example = "132000")
    private BigDecimal netBenefit;

    @ApiModelProperty(
            value = "카테고리별 혜택 금액",
            example = "{\"교통\":5000.0,\"카페/디저트\":3000.0}"
    )
    private Map<String, BigDecimal> categoryBenefits;

    @ApiModelProperty(
            value = "추천 이유 리스트",
            example = "[\"전월 실적 충족 가능성 높음\",\"교통 지출 비중 큼\"]"
    )
    private List<String> recommendationReasons;

    @ApiModelProperty(value = "조건 충족 가능성(0.0~1.0)", example = "0.83")
    private Double conditionFulfillmentProbability;

    @ApiModelProperty(value = "예상 달성률(0.0~1.0)", example = "0.70")
    private Double expectedAchievementRate;

    @ApiModelProperty(
            value = "주요 혜택 카테고리",
            example = "[\"교통\",\"카페/디저트\"]"
    )
    private List<String> mainBenefitCategories;
    
    // 좋아요/즐겨찾기 상태 필드
    @ApiModelProperty(value = "사용자 좋아요 여부", example = "true")
    @JsonProperty("is_liked")
    private Boolean liked;

    @ApiModelProperty(value = "좋아요 수", example = "42")
    @JsonProperty("like_count")
    private Integer likeCount;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    @JsonProperty("is_starred")
    private Boolean starred;
}