package team2.pjt12.matchumoney.domain.savingdetail.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "SavingDetailResponse", description = "적금 상품 상세 조회 응답 DTO")
public class SavingDetailResponseDTO {

    @ApiModelProperty(value = "적금 상품 ID", example = "1")
    private Long savingProductId;

    @ApiModelProperty(value = "공시월", example = "202301")
    private String dclsMonth;

    @ApiModelProperty(value = "은행 이름", example = "우리은행")
    private String korCoNm;

    @ApiModelProperty(value = "금융 상품 이름", example = "우리SUPER주거래적금")
    private String finPrdtNm;

    @ApiModelProperty(value = "가입 방법", example = "영업점, 인터넷, 스마트폰")
    private String joinWay;

    @ApiModelProperty(value = "만기 후 이자율", example = "만기시점 기준 금리 적용")
    private String mtrtInt;

    @ApiModelProperty(value = "우대 조건", example = "자동이체 시 +0.2%...")
    private String spclCnd;

    @ApiModelProperty(value = "가입 제한", example = "1")
    private String joinDeny;

    @ApiModelProperty(value = "가입 대상", example = "개인")
    private String joinMember;

    @ApiModelProperty(value = "기타 유의사항", example = "- 1인 1계좌...")
    private String etcNote;

    @ApiModelProperty(value = "가입 한도", example = "200000")
    private String maxLimit;

    @ApiModelProperty(value = "공시 시작일", example = "20230101")
    private String dclsStrtDay;

    @ApiModelProperty(value = "공시 종료일", example = "20231231")
    private String dclsEndDay;

    @ApiModelProperty(value = "금융사 제출일", example = "2023-01-05")
    private String finCoSubmDay;

    @ApiModelProperty(value = "상품 유형", example = "2")
    private String productType;

    @ApiModelProperty(value = "금융회사 코드", example = "0010001")
    private String finCoNo;

    @ApiModelProperty(value = "금융 상품 코드", example = "10141114300011")
    private String finPrdtCd;

    @ApiModelProperty(value = "금융사 내부 ID", example = "16")
    private Long finId;

    @ApiModelProperty(value = "페르소나 ID", example = "5")
    private Long personaId;

    @ApiModelProperty(value = "사용자 ID", example = "1")
    private Long userId;

    @ApiModelProperty(value = "좋아요 여부", example = "true")
    private boolean liked;

    @ApiModelProperty(value = "좋아요 개수", example = "42")
    private int likeCount;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    private Boolean isStarred;

    @ApiModelProperty(value = "상품 신청 URL", example = "https://wooribank.com/product/123")
    private String requestUrl;

    @ApiModelProperty(value = "개월별 적금 금리 목록")
    private List<SavingOptionDTO> options;
}