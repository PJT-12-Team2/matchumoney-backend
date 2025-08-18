package team2.pjt12.matchumoney.domain.cardrecommendation.vo;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardProductVO {
    private Integer cardProductId; // 카드 ID (카드고릴라 idx)
    private String name; // 카드 이름
    private String type; // 카드 종류 (신용, 체크, unknown)
    private Boolean isAvailable; // 발급 가능 여부
    private Integer issuerId; // 발급사 ID (FK)
    private String annualFee; // 연회비 정보
    private Long preMonthMoney; // 전월 실적 금액 (원)
    private Boolean onlineOnly; // 온라인 전용 카드 여부
    private String cardImageUrl; // 카드 이미지 URL
    private String requestPcUrl; // PC 신청 링크
    private String requestMobileUrl; // 모바일 신청 링크
    private String annualFeeDetail; // 연회비 상세 설명 (HTML 디코딩 텍스트)
    private String corpPrContainer; // 카드사 PR 컨테이너 (HTML 디코딩 텍스트)
    private String corpPrDetail; // 카드사 PR 상세 (원시 HTML)
    private String corpTips; // 고릴라 TIP (HTML 디코딩 텍스트)
    private Timestamp createdTime;
    private Timestamp lastModifiedTime;
    private Long personaId;
    private String issuer; // 발급사명
    private Long holdingId; // 카드 보유 ID (매칭되지 않은 카드의 경우)
}