package team2.pjt12.matchumoney.domain.mydata.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "카드 매칭 결과 DTO")
public class CardMatchingResultDTO {
    
    @ApiModelProperty(value = "전체 처리 대상 건수", example = "10", position = 1)
    private int totalCount;
    
    @ApiModelProperty(value = "매칭 성공 건수", example = "8", position = 2)
    private int successCount;
    
    @ApiModelProperty(value = "매칭 실패 건수", example = "2", position = 3)
    private int failCount;
    
    @ApiModelProperty(value = "처리 결과 메시지", example = "매칭 완료 - 성공: 8건, 실패: 2건", position = 4)
    private String message;
    
    @ApiModelProperty(value = "성공률 (%)", example = "80.0", position = 5)
    private Double successRate;
    
    public static CardMatchingResultDTO success(int totalCount, int successCount) {
        int failCount = totalCount - successCount;
        double successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0.0;
        
        return CardMatchingResultDTO.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failCount(failCount)
                .successRate(Math.round(successRate * 100.0) / 100.0)
                .message(String.format("매칭 완료 - 성공: %d건, 실패: %d건 (성공률: %.1f%%)", 
                        successCount, failCount, successRate))
                .build();
    }
    
    public static CardMatchingResultDTO fail(String message) {
        return CardMatchingResultDTO.builder()
                .totalCount(0)
                .successCount(0)
                .failCount(0)
                .successRate(0.0)
                .message(message)
                .build();
    }
}
