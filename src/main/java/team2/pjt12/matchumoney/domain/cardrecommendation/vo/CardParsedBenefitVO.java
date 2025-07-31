package team2.pjt12.matchumoney.domain.cardrecommendation.vo;

import lombok.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardParsedBenefitVO {
    private Integer parsedBenefitId; // 파싱된 혜택 ID
    private Integer cardId2; // 카드 ID (FK)
    private String title; // 혜택 원본 제목
    private String category; // 추론된 혜택 카테고리 (교통, 외식 등)
    private String benefitType; // 혜택 유형 (할인, 적립, 캐시백)
    private BigDecimal value; // 혜택 값 (퍼센트 또는 금액)
    private String conditionText; // 혜택 조건 (콤마로 구분된 텍스트)
    private Integer maxBenefitMonthly; // 월 최대 혜택 금액 (무제한 시 NULL)
    private Integer minSpendPerTransaction; // 건당 최소 결제 금액
    private Integer preMonthMoneySpecific; // 해당 혜택 전월 실적 (NULL 시 카드 전체 전월 실적 따름)
    private String description; // 혜택 상세 설명 (HTML 디코딩 텍스트)
    private Timestamp createdTime;
    private Timestamp lastModifiedTime;
}