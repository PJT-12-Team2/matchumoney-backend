package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class WithdrawRequestDTO {

    @NotBlank
    private final String reason;

    // 상세 사유 (선택: '기타(직접 입력)'일 경우만 입력)
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
