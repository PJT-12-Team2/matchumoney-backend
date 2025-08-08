package team2.pjt12.matchumoney.domain.depositsearch.dto;

import lombok.Data;

import java.util.List;

@Data
public class DepositSearchResponseDTO {
    private Long depositProductId;
    private String korCoNm;
    private String finPrdtCd;
    private String finPrdtNm;
    private String maxLimit;
    private String benefit;
    private String personaType;
    private List<DepositOptionDTO> depositOptions;  // ✅ 금리 옵션 리스트 추가
    private Boolean isStarred;
}