package team2.pjt12.matchumoney.domain.deposit.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 예금 상품 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositProductResponseDTO {
    private String bankName;            // 은행명 (kor_co_nm)
    private String productName;         // 상품명 (fin_prdt_nm)
    private String etcNote;             // 기타유의사항 (etc_note)
    private Integer maxSaveTrm;         // 최대 저축 기간
    private BigDecimal maxIntrRate;     // 최대 기본금리 (intr_rate)
    private BigDecimal maxIntrRate2;    // 최대 우대금리 (intr_rate2)
    private String minAmount;           // 최소 금액 (ServiceImpl에서 etcNote로부터 추출)
}