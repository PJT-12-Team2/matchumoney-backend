package team2.pjt12.matchumoney.domain.deposit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// 예금 상품 정보 VO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositVO {
    private String finPrdtCd;           // 금융상품코드 (Primary Key)
    private String korCoNm;             // 은행명
    private String finPrdtNm;           // 상품명
    private String joinWay;             // 가입방법
    private String mtrtInt;             // 만기후이자율
    private String spclCnd;             // 특별조건
    private Integer joinDeny;           // 가입제한
    private String joinMember;          // 가입대상
    private String etcNote;             // 기타유의사항
    private BigDecimal maxLimit;        // 최고한도
    private Integer dclsStrtDay;        // 공시시작일
    private BigDecimal dclsEndDay;      // 공시종료일
    private Long finCoSubmDay;          // 금융회사제출일
    private List<DepositOptionVO> depositOptions; // 연결된 옵션 목록
}
