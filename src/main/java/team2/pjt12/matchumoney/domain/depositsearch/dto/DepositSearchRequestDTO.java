package team2.pjt12.matchumoney.domain.depositsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "예금 검색 요청 DTO")
public class DepositSearchRequestDTO {
    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private Long userId; // 인증 기반 userId 사용. 서버에서 무시 권장

    @ApiModelProperty(value = "은행명(부분 일치 가능)", example = "국민은행")
    private String korCoNm;

    @ApiModelProperty(value = "가입 한도 상한(원)", example = "10000000")
    private Integer maxLimit;
}