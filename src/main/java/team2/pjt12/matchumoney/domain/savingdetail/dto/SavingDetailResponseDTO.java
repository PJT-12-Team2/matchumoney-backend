package team2.pjt12.matchumoney.domain.savingdetail.dto;

import lombok.Data;

import java.util.List;

@Data
public class SavingDetailResponseDTO {

    private Long savingProductId;
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
    // 1:N 관계
    private List<SavingOptionDTO> options;
}