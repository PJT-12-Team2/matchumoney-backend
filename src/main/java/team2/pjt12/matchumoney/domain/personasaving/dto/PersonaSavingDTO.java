package team2.pjt12.matchumoney.domain.personasaving.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "추천 적금 상품 정보 DTO")
public class PersonaSavingDTO {
    @ApiModelProperty(value = "적금 상품 ID", example = "1")
    private Long savingId; // saving_product_id (saving_product)

    @ApiModelProperty(value = "금융사", example = "국민은행")
    private String bankName; // kor_co_nm (saving_product)

    @ApiModelProperty(value = "적금 상품명", example = "KB내맘대로적금")
    private String savingName; // fin_prdt_nm (saving_product)

    @ApiModelProperty(value = "기본 금리", example = "2.5")
    private Double basicRate; // intr_rate (saving_option)

    @ApiModelProperty(value = "최고 금리", example = "3.2")
    private Double maxRate; // intr_rate2 (saving_option)

    @ApiModelProperty(value = "금융사 로고 이미지 URL", example = "/src/assets/bankLogo_images/BK_KB_Profile.png")
    private String companyImage; // company_image (financial_companies)

    @ApiModelProperty(value = "매월 적금 가능 금액", example = "1000000")
    private Long maxLimit;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "true")
    private Boolean isStarred;

    @ApiModelProperty(value = "좋아요 여부", example = "true")
    private Boolean isLiked; // 좋아요 여부

    @ApiModelProperty(value = "좋아요 수", example = "100")
    private Integer likeCount; // 좋아요 수
}
