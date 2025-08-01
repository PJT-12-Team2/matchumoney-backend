package team2.pjt12.matchumoney.domain.cardrecommendation.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryStatDTO {
    private String category; // 카테고리명
    private Long totalAmount; // 해당 카테고리 총 사용금액
    private Integer transactionCount; // 해당 카테고리 거래 건수
    private BigDecimal averageAmount; // 해당 카테고리 평균 거래금액
    private BigDecimal categoryRatio; // 전체 사용액 대비 해당 카테고리 비율 (%)
}