package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBenefitDTO {
    private Integer cardId; // 카드 ID
    private String cardName; // 카드명
    private String cardType; // 카드 종류 (신용, 체크)
    private String issuer; // 발급사
    private Long estimatedBenefit; // 예상 혜택 금액 (BIGINT)
    private String annualFee; // 연회비
    private Long preMonthMoney; // 전월 실적 조건 (BIGINT)
    private String cardImageUrl; // 카드 이미지 URL
    private String requestPcUrl; // PC 신청 링크
    private String requestMobileUrl; // 모바일 신청 링크
    
    // 추천 시스템을 위한 추가 필드
    private Double recommendationScore; // 추천 점수 (0-100)
    private java.math.BigDecimal expectedMonthlyBenefit; // 예상 월 혜택 금액
    private java.math.BigDecimal expectedYearlyBenefit; // 예상 연 혜택 금액
    private java.math.BigDecimal netBenefit; // 순 혜택 (연 혜택 - 연회비)
    private java.util.Map<String, java.math.BigDecimal> categoryBenefits; // 카테고리별 혜택 금액
    private java.util.List<String> recommendationReasons; // 추천 이유
    private Double conditionFulfillmentProbability; // 조건 충족 가능성 (0.0~1.0)
    private Double expectedAchievementRate; // 사용자의 예상 달성률 (0.0~1.0)
    private java.util.List<String> mainBenefitCategories; // 주요 혜택 카테고리
    
    // 좋아요/즐겨찾기 상태 필드
    @JsonProperty("is_liked")
    private Boolean liked; // 좋아요 상태
    @JsonProperty("like_count")
    private Integer likeCount; // 좋아요 개수
    @JsonProperty("is_starred")
    private Boolean starred; // 즐겨찾기 상태
}