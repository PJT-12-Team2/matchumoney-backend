package team2.pjt12.matchumoney.domain.savingsearch.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SavingSearchResponse", description = "적금 상품 검색 조회 DTO")
public class SavingSearchResponseDTO {
    @ApiModelProperty(value = "적금 상품 ID", example = "1")
    private Long savingProductId;

    @ApiModelProperty(value = "은행 이름", example = "우리은행")
    private String korCoNm;

    @ApiModelProperty(value = "금융 상품 코드", example = "WR0001F")
    private String finPrdtCd;

    @ApiModelProperty(value = "금융 상품 이름", example = "우리SUPER주거래적금")
    private String finPrdtNm;

    @ApiModelProperty(value = "최대 한도", example = "1000000")
    private String maxLimit;

    @ApiModelProperty(value = "부가 혜택", example = "1. 거래실적 인정기간 동안 우리은행 입출식 계좌에서 아래 각 항목별 실적...")
    private String benefit;

    @ApiModelProperty(value = "추천 페르소나 유형", example = "개미")
    private String personaType;

    @ApiModelProperty(value = "저축 금리 목록")
    private List<SavingOptionDTO> savingOptions;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "true")
    private Boolean isStarred;

    @JsonIgnore
    @ApiModelProperty(value = "사용자 고유 ID", example = "1", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "좋아요 여부", example = "false")
    private Boolean isLiked;

    @ApiModelProperty(value = "좋아요 개수", example = "25")
    private Integer likeCount;
}
