package team2.pjt12.matchumoney.domain.deposit.dto.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// 예금 상품 응답 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "예금 상품 응답 DTO")
public class DepositProductResponseDTO {
    @ApiModelProperty(value = "예금 상품 ID(내부 PK)", example = "12")
    private Long id;

    @ApiModelProperty(value = "은행명", example = "국민은행")
    private String bankName; // (kor_co_nm)

    @ApiModelProperty(value = "상품명", example = "KB푸른바다예금")
    private String productName; // (fin_prdt_nm)

    @ApiModelProperty(value = "기타 유의사항", example = "디지털채널 전용 상품")
    private String etcNote; // (etc_note)

    @ApiModelProperty(value = "최대 저축 기간(개월)", example = "36")
    private Integer maxSaveTrm;

    @ApiModelProperty(value = "최대 기본금리(%)", example = "2.5")
    private BigDecimal maxIntrRate; // (intr_rate)

    @ApiModelProperty(value = "최대 우대금리(%)", example = "2.9")
    private BigDecimal maxIntrRate2; // (intr_rate2)

    @ApiModelProperty(value = "최소 가입 금액", example = "1,000,000원")
    private String minAmount; // ServiceImpl에서 etcNote로부터 추출

    @ApiModelProperty(value = "예금 상품 원천 ID", example = "345")
    private Long depositProductId;

    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    private Boolean isFavorite;

    @ApiModelProperty(value = "좋아요 여부", example = "true")
    private Boolean liked;

    @ApiModelProperty(value = "좋아요 개수", example = "7")
    private Integer likeCount;
}
