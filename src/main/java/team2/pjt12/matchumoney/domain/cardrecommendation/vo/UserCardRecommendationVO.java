package team2.pjt12.matchumoney.domain.cardrecommendation.vo;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCardRecommendationVO {
    private Long recommendationId; // 추천 ID (AUTO_INCREMENT)
    private Long userId; // 사용자 ID
    private Integer baseCardId; // 기준이 되는 사용자 보유 카드 ID
    private Integer cardId; // 추천된 카드 ID (card_product.card_product_id)
    private String cardName; // 카드명
    private String cardType; // 카드 종류 (신용, 체크)
    private String issuer; // 발급사
    private Long estimatedBenefit; // 예상 혜택 (월간 예상, BIGINT)
    private String annualFee; // 연회비
    private Long preMonthMoney; // 전월 실적 조건 (BIGINT)
    private String cardImageUrl; // 카드 이미지 URL
    private String requestPcUrl; // PC 신청 링크
    private String requestMobileUrl; // 모바일 신청 링크
    private Timestamp recommendedAt; // 추천 생성일시
}