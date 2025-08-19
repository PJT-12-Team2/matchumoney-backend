package team2.pjt12.matchumoney.domain.compare.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "카드 상품 정보 DTO")
public class CompareCardResponseDTO {

    @ApiModelProperty(value = "상품 이미지 URL", example = "https://d1c5n4ri2guedi.cloudfront.net/card/1/card_img/20083/1card.png")
    private String productImage;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    private Boolean isStarred;

    @ApiModelProperty(value = "카드 상품 ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "회사 이름", example = "신한카드")
    private String korCoName;

    @ApiModelProperty(value = "상품 이름", example = "신한카드 Hi-Point")
    private String finPrdtName;

    @ApiModelProperty(value = "카드 종류", example = "신용")
    private String type;

    @ApiModelProperty(value = "연회비", example = "해외겸용 8,000원")
    private String annualFee;

    @ApiModelProperty(value = "전월 실적 기준 금액", example = "200000")
    private Integer preMonthMoney;

    @ApiModelProperty(value = "카드 혜택 옵션 리스트",
            example = "[{\"parsedBenefitId\": 1, \"cardId2\": 10, \"title\": \"쇼핑\", \"category\": \"온라인쇼핑\", " +
                    "\"benefitType\": \"적립\", \"value\": 5, \"conditionText\": \"온라인\", \"maxBenefitMonthly\": 0, \"minSpendPerTransaction\": 0, \"preMonthMoneySpecific\": null}]")
    private List<CardOptionDTO> benefits;
}
