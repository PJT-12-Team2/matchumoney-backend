package team2.pjt12.matchumoney.domain.user.dto.req;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class UpdatePasswordRequestDTO {

    @NotEmpty
    public String currentPassword;

    @NotEmpty
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
            message = "비밀번호는 영문자·숫자·특수문자를 모두 포함해야 합니다."
    )
    public String newPassword;

    @NotEmpty
    public String confirmPassword;
}
