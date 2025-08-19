package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@ApiModel(description = "로그인 요청 DTO")
public class LoginRequestDTO {

    @ApiModelProperty(value = "로그인 이메일", example = "user@example.com", required = true)
    @NotEmpty
    @Email
    private final String email;

    @ApiModelProperty(value = "비밀번호", example = "P@ssw0rd!", required = true)
    @NotEmpty
    private final String password;

    @JsonCreator
    public LoginRequestDTO(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }
}