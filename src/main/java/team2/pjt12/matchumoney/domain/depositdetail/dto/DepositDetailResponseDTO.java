package team2.pjt12.matchumoney.domain.depositdetail.dto;

import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "예금 상품 ID", example = "10")
    private Long depositProductId;

    @ApiModelProperty(value = "공시월(YYYYMM)", example = "202501")
    private String dclsMonth;

    @ApiModelProperty(value = "금융사", example = "국민은행")
    private String korCoNm;

    @ApiModelProperty(value = "상품명", example = "KB푸른바다예금")
    private String finPrdtNm;

    @ApiModelProperty(value = "가입 방법", example = "인터넷, 스마트폰")
    private String joinWay;

    @ApiModelProperty(value = "만기 후 이율", example = "만기 후 1개월 이내: 약정이율×50% ...")
    private String mtrtInt;

    @ApiModelProperty(value = "우대조건", example = "1. 모바일뱅킹 첫거래 고객 0.10% ...")
    private String spclCnd;

    @ApiModelProperty(value = "가입 제한(코드)", example = "1")
    private String joinDeny;

    @ApiModelProperty(value = "가입 대상", example = "실명의 개인")
    private String joinMember;

    @ApiModelProperty(value = "기타 유의사항", example = "가입금액 : 1백만원 이상")
    private String etcNote;

    @ApiModelProperty(value = "가입 한도", example = "10000000")
    private String maxLimit;

    @ApiModelProperty(value = "공시 시작일", example = "20250721")
    private String dclsStrtDay;

    @ApiModelProperty(value = "공시 종료일", example = "99991231")
    private String dclsEndDay;

    @ApiModelProperty(value = "제출일시", example = "202507211125")
    private String finCoSubmDay;

    @ApiModelProperty(value = "상품 유형", example = "1")
    private String productType; // 예금 : 1

    @ApiModelProperty(value = "금융회사 코드", example = "0013175")
    private String finCoNo;

    @ApiModelProperty(value = "상품 코드", example = "WR0001B")
    private String finPrdtCd;

    @ApiModelProperty(value = "금융사 ID", example = "3")
    private Long finId;

    @ApiModelProperty(value = "사용자 페르소나 ID", example = "2")
    private Long personaId;

    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "현재 사용자 좋아요 여부", example = "true")
    private Boolean liked;

    @ApiModelProperty(value = "좋아요 개수", example = "12")
    private Integer likeCount;

    @ApiModelProperty(value = "신청 URL", example = "https://bank.example.com/apply/10")
    private String requestUrl;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    private Boolean isStarred;

    @ApiModelProperty(value = "예금 옵션(기간/금리 등)")
    private List<DepositOptionDTO> options; // 1:N 관계
}
