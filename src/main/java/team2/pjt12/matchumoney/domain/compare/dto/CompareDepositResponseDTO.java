package team2.pjt12.matchumoney.domain.compare.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@ApiModel(description = "예금 상품 비교 응답 DTO")
public class CompareDepositResponseDTO {

    @ApiModelProperty(value = "예금 상품 ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "은행 이름", example = "국민은행")
    private String korCoName;

    @ApiModelProperty(value = "상품 이름", example = "KB푸른바다예금")
    private String finPrdtName;

    @ApiModelProperty(value = "가입 방법", example = "인터넷, 스마트폰")
    private String joinWay;

    @ApiModelProperty(value = "만기 후 이율", example = "만기 후 - 1개월이내: 약정이율×50% ...")
    private String maturityInterest;

    @ApiModelProperty(value = "우대 조건", example = "신규 고객 우대 이율 제공 등")
    private String specialCondition;

    @ApiModelProperty(value = "가입 대상", example = "실명의 개인")
    private String joinMember;

    @ApiModelProperty(value = "기타 사항", example = "디지털채널 전용 상품")
    private String etcNote;

    @ApiModelProperty(value = "가입 한도", example = "10000000")
    private BigDecimal maxLimit;

    @ApiModelProperty(value = "회사 로고 이미지 URL", example = "/src/assets/bank-Logos/BK_KB_Profile.png")
    private String companyImage;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    private Boolean isStarred;

    @ApiModelProperty(value = "금리 리스트")
    private List<RateDTO> rates;
}
