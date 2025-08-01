package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import lombok.*;

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
}