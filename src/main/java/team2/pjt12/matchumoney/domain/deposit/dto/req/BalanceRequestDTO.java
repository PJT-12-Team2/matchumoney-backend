package team2.pjt12.matchumoney.domain.deposit.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "잔액 기반 예금 추천 요청 DTO")
public class BalanceRequestDTO {
    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private String userId;

    @ApiModelProperty(value = "선택된 계좌 잔액(원)", example = "1000000", required = true)
    @NotNull
    @Min(0)
    private Long balance;

    @ApiModelProperty(value = "선택된 계좌번호(옵션, 로그용)", example = "123-456-789012")
    private String accountNumber;

    public BalanceRequestDTO(String userId, Long balance) {
        this.userId = userId;
        this.balance = balance;
    }
}