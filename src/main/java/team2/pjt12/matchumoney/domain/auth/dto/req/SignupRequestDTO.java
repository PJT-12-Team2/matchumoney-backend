package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Setter
@Getter
@ApiModel(description = "회원가입 요청 DTO")
public class SignupRequestDTO {

    @ApiModelProperty(value = "사용자 닉네임", example = "머니머니", required = true)
    @NotEmpty
    private final String nickname;

    @ApiModelProperty(value = "로그인 이메일", example = "user@example.com", required = true)
    @NotEmpty
    @Email
    private final String email;

    @ApiModelProperty(
            value = "비밀번호(영문/숫자/특수문자 포함 8~20자)",
            example = "P@ssw0rd!",
            required = true
    )
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
            message = "비밀번호는 영문자·숫자·특수문자를 모두 포함해야 합니다."
    )
    @NotEmpty
    private final String password;

    @ApiModelProperty(value = "비밀번호 확인", example = "P@ssw0rd!", required = true)
    @NotEmpty
    private final String passwordCheck;

    @ApiModelProperty(value = "프로필 이미지 URL", example = "https://cdn.example.com/profile.png")
    private final String profileImageUrl;

    @JsonCreator
    public SignupRequestDTO(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("passwordCheck") String passwordCheck,
            @JsonProperty("profileImageUrl") String profileImageUrl) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.profileImageUrl = profileImageUrl;
    }
}