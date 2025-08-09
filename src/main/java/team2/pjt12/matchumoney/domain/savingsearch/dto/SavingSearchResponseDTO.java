package team2.pjt12.matchumoney.domain.savingsearch.dto;

import lombok.Data;
import java.util.List;

@Data
public class SavingSearchResponseDTO {
    private Long savingProductId;
    private String korCoNm;
    private String finPrdtCd;
    private String finPrdtNm;
    private String maxLimit;
    private String benefit;
    private String personaType;
    private List<SavingOptionDTO> savingOptions;  // ✅ 금리 옵션 리스트 추가
    private Boolean isStarred;
    private Long userId;
    private Boolean isLiked;    // ✅ 꼭 Boolean로 (null 가능)
    private Integer likeCount;  // ✅ 0도 찍히도록 int도 가능하지만 Integer 권장
}