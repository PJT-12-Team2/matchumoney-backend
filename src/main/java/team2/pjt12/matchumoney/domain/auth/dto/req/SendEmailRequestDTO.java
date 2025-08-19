package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@ApiModel(description = "이메일 인증/발송 요청 DTO")
public class SendEmailRequestDTO {

    @ApiModelProperty(value = "로그인 이메일", example = "user@example.com", required = true)
    @NotEmpty
    @Email
    private final String email;

    @JsonCreator
    public SendEmailRequestDTO(@JsonProperty("email") String email) {
        this.email = email;
    }
}