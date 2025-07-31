package team2.pjt12.matchumoney.domain.deposit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 예금 옵션 정보 VO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositOptionVO {
    private Long id;                    // 옵션 ID
    private String intrRateType;        // 금리유형코드
    private String intrRateTypeNm;      // 금리유형명
    private Integer saveTrm;            // 저축기간 (개월)
    private BigDecimal intrRate;        // 기본금리
    private BigDecimal intrRate2;       // 최고우대금리
    private Integer optionId;           // 옵션순번
    private String finPrdtCd;           // 금융상품코드 (외래키)
    private Integer depositProductId;   // 예금상품ID
}