package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@ApiModel(description = "비밀번호 재설정 요청 DTO")
public class ResetRequestDTO {

    @ApiModelProperty(value = "로그인 이메일", example = "user@example.com", required = true)
    @NotEmpty
    @Email
    private final String email;

    @ApiModelProperty(
            value = "새 비밀번호(영문/숫자/특수문자 포함 8~20자)",
            example = "P@ssw0rd!",
            required = true
    )
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
            message = "비밀번호는 영문자·숫자·특수문자를 모두 포함해야 합니다."
    )
    @NotEmpty
    private final String newPassword;

    @ApiModelProperty(value = "새 비밀번호 확인", example = "P@ssw0rd!", required = true)
    @NotEmpty
    private final String confirmPassword;

    @JsonCreator
    public ResetRequestDTO(
            @JsonProperty ("email") String email,
            @JsonProperty("newPassword") String newPassword,
            @JsonProperty("confirmPassword") String confirmPassword) {
        this.email = email;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}