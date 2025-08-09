package team2.pjt12.matchumoney.domain.depositdetail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DepositDetailResponseDTO {

    private Long depositProductId;
    private String dclsMonth;
    private String korCoNm;
    private String finPrdtNm;
    private String joinWay;
    private String mtrtInt;
    private String spclCnd;
    private String joinDeny;
    private String joinMember;
    private String etcNote;
    private String maxLimit;
    private String dclsStrtDay;
    private String dclsEndDay;
    private String finCoSubmDay;
    private String productType;
    private String finCoNo;
    private String finPrdtCd;
    private Long finId;
    private Long personaId;
    private Long userId;
    private boolean liked;
    private int likeCount;
    private String requestUrl;

    // 1:N 관계
    private List<DepositOptionDTO> options;
}