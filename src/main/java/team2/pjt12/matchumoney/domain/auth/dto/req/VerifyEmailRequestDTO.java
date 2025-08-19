package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@ApiModel(description = "이메일 인증코드 검증 요청 DTO")
public class VerifyEmailRequestDTO {

    @ApiModelProperty(value = "로그인 이메일", example = "user@example.com", required = true)
    @NotBlank
    @Email
    private final String email;

    @ApiModelProperty(value = "인증 코드(6자리 숫자)", example = "123456", required = true)
    @NotBlank
    private final String code;

    @JsonCreator
    public VerifyEmailRequestDTO(@JsonProperty("email") String email,
                                 @JsonProperty("code") String code) {
        this.email = email;
        this.code = code;
    }
}