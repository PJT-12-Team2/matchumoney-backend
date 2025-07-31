package team2.pjt12.matchumoney.domain.personadeposit.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "추천 예금 상품 정보 DTO")
public class PersonadepositDTO {
    @ApiModelProperty(value = "예금 상품 ID", example = "1")
    private Long depositId; // deposit_product_id (deposit_product)

    @ApiModelProperty(value = "금융사 이름", example = "국민은행")
    private String bankName; // kor_co_nm (deposit_product)

    @ApiModelProperty(value = "예금 상품명", example = "KB Star 정기예금")
    private String productName; // fin_prdt_nm (deposit_product)

    @ApiModelProperty(value = "기본 금리", example = "2.2")
    private Double basicRate; // intr_rate (deposit_option)

    @ApiModelProperty(value = "최대 금리", example = "3.0")
    private Double maxRate; // intr_rate2 (deposit_option)

    @ApiModelProperty(value = "금융사 로고 이미지 URL", example = "https://img.kbstar.com/logo.png")
    private String companyImage; // company_image (financial_companies)
}
