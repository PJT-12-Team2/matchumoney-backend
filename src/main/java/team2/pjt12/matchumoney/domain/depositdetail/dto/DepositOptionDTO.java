package team2.pjt12.matchumoney.domain.depositdetail.dto;

import lombok.Data;

@Data
public class DepositOptionDTO {

    private Long depositOptionId;
    private String intrRateType;
    private String intrRateTypeNm;
    private String saveTrm;
    private String intrRate;
    private String intrRate2;
    private Long depositProductId; // foreign key
    private String finPrdtCd;
    private Long finId;
}