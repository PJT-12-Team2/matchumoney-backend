package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "소셜 로그인 요청 DTO (카카오 인가 코드 전달)")
public class SocialLoginRequestDTO {

    @ApiModelProperty(value = "카카오 로그인 후 전달받은 인가 코드", required = true, example = "3puuS2zO0uvNS8PaFTdp...")
    private String code;

    @JsonCreator
    public SocialLoginRequestDTO(@JsonProperty("code") String code) {
        this.code = code;
    }
}