package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@ApiModel(description = "회원 탈퇴 요청 DTO")
public class WithdrawRequestDTO {

    @ApiModelProperty(value = "탈퇴 사유", example = "서비스가 마음에 들지 않음", required = true)
    @NotBlank
    private final String reason;

    @ApiModelProperty(
            value = "상세 사유(선택). 사유가 '기타'일 때는 필수",
            example = "다른 서비스로 이전합니다.",
            required = false
    )
    private final String detail;

    @JsonCreator
    public WithdrawRequestDTO(
            @JsonProperty("reason") String reason,
            @JsonProperty("detail") String detail
    ) {
        this.reason = reason;
        this.detail = detail;
    }
}
