package team2.pjt12.matchumoney.domain.savingdetail.dto;

import lombok.Data;

@Data
public class SavingOptionDTO {

    private Long savingOptionId;
    private String intrRateType;
    private String intrRateTypeNm;
    private String saveTrm;
    private String intrRate;
    private String intrRate2;
    private String finPrdtCd;
    private Long finId;
}