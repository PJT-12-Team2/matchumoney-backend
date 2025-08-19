package team2.pjt12.matchumoney.domain.depositsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "예금 검색 응답 DTO")
public class DepositSearchResponseDTO {
    @ApiModelProperty(value = "예금 상품 ID", example = "10")
    private Long depositProductId;

    @ApiModelProperty(value = "은행명", example = "국민은행")
    private String korCoNm;

    @ApiModelProperty(value = "상품 코드", example = "WR0001B")
    private String finPrdtCd;

    @ApiModelProperty(value = "상품명", example = "KB푸른바다예금")
    private String finPrdtNm;

    @ApiModelProperty(value = "가입 한도", example = "10000000")
    private String maxLimit;

    @ApiModelProperty(value = "우대 조건", example = "자동이체시 우대금리 제공")
    private String benefit;

    @ApiModelProperty(value = "페르소나 타입", example = "거북이")
    private String personaType;

    @ApiModelProperty(value = "금리 옵션 리스트")
    private List<DepositOptionDTO> depositOptions;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    private Boolean isStarred;

    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "사용자 좋아요 여부", example = "true")
    private Boolean isLiked;

    @ApiModelProperty(value = "좋아요 수", example = "7")
    private Integer likeCount;
}