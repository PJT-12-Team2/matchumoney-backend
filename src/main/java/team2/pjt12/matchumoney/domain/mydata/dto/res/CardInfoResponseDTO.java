package team2.pjt12.matchumoney.domain.mydata.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "카드 정보 응답 DTO")
public class CardInfoResponseDTO {
    
    @ApiModelProperty(value = "카드 보유 ID", example = "1", position = 1)
    private Long holdingId;
    
    @ApiModelProperty(value = "카드고릴라 카드 ID (매칭된 경우)", example = "146", position = 2)
    private Integer cardId;
    
    @ApiModelProperty(value = "금융기관 ID", example = "1665624378", position = 3)
    private Long finId;
    
    @ApiModelProperty(value = "카드명", example = "The Easy카드", position = 4)
    private String cardName;
    
    @ApiModelProperty(value = "마스킹된 카드번호", example = "1234-****-5678", position = 5)
    private String maskedCardNo;
    
    @ApiModelProperty(value = "카드 타입", example = "신용", position = 6)
    private String cardType;
    
    @ApiModelProperty(value = "카드 상태", example = "정상", position = 7)
    private String cardState;
    
    @ApiModelProperty(value = "휴면 여부", example = "N", position = 8)
    private String sleepYn;
    
    @ApiModelProperty(value = "교통카드 기능", example = "N", position = 9)
    private String trafficYn;
    
    @ApiModelProperty(value = "카드 이미지 URL", position = 10)
    private String imageUrl;
    
    @ApiModelProperty(value = "발급일 (YYYYMMDD)", example = "20250101", position = 11)
    private String issueDate;
    
    @ApiModelProperty(value = "유효기간 (YYYYMM)", example = "20301130", position = 12)
    private String validPeriod;
    
    @ApiModelProperty(value = "매칭 상태", example = "MATCHED", position = 13)
    private String matchStatus;
    
    @ApiModelProperty(value = "카드고릴라 매칭된 카드명", example = "The Easy카드", position = 14)
    private String matchedCardName;
}
