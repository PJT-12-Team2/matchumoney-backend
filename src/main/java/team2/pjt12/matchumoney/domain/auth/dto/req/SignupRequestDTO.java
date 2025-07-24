package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class SignupRequestDTO {

    @NotEmpty
    private final String nickname;

    @NotEmpty
    @Email
    private final String email;

    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
            message = "비밀번호는 영문자·숫자·특수문자를 모두 포함해야 합니다."
    )
    @NotEmpty
    private final String password;

    @NotEmpty
    private final String passwordCheck;

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