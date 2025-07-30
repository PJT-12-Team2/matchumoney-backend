package team2.pjt12.matchumoney.domain.mydata.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "카드 거래 내역 응답 DTO")
public class CardTransactionResponseDTO {
    
    @ApiModelProperty(value = "거래 ID", example = "12345", position = 1)
    private Long transactionId;
    
    @ApiModelProperty(value = "카드 보유 ID", example = "1", position = 2)
    private Long holdingId;
    
    @ApiModelProperty(value = "카드명", example = "Xxx 카드", position = 3)
    private String cardName;
    
    @ApiModelProperty(value = "거래일자 (YYYYMMDD)", example = "20250124", position = 4)
    private String transactionDate;
    
    @ApiModelProperty(value = "거래시간 (HHMMSS)", example = "143000", position = 5)
    private String transactionTime;
    
    @ApiModelProperty(value = "가맹점명", example = "스타벅스 강남점", position = 6)
    private String merchantName;
    
    @ApiModelProperty(value = "소비 분야", example = "카페", position = 7)
    private String merchantCategory;
    
    @ApiModelProperty(value = "거래금액", example = "4500", position = 8)
    private Long amount;
    
    @ApiModelProperty(value = "결제구분", example = "일시불", position = 9)
    private String paymentType;
    
    @ApiModelProperty(value = "할부개월", example = "00", position = 10)
    private String installmentMonth;
    
    @ApiModelProperty(value = "승인번호", example = "12345678", position = 11)
    private String approvalNo;
    
    @ApiModelProperty(value = "취소여부", example = "N", position = 12)
    private String cancelYn;
    
    @ApiModelProperty(value = "취소금액", example = "0", position = 13)
    private Long cancelAmount;
    
    @ApiModelProperty(value = "부가세", example = "0", position = 14)
    private Long vat;
    
    @ApiModelProperty(value = "캐시백", example = "45", position = 15)
    private Long cashBack;
    
    @ApiModelProperty(value = "해외/국내 구분", example = "국내", position = 16)
    private String homeForeignType;
    
    @ApiModelProperty(value = "가맹점 주소", position = 17)
    private String merchantAddress;
    
    @ApiModelProperty(value = "가맹점 전화번호", position = 18)
    private String merchantPhone;
}
