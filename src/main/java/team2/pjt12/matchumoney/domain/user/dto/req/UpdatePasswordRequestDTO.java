package team2.pjt12.matchumoney.domain.user.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@ApiModel(value = "UpdatePasswordRequest", description = "비밀번호 변경 요청 DTO")
public class UpdatePasswordRequestDTO {

    @NotEmpty
    @ApiModelProperty(value = "현재 비밀번호", example = "oldPassword123!", required = true)
    public String currentPassword;

    @NotEmpty
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
            message = "비밀번호는 영문자·숫자·특수문자를 모두 포함해야 합니다."
    )
    @ApiModelProperty(
            value = "새 비밀번호 (영문자·숫자·특수문자 포함 필수)",
            example = "NewPass123!",
            required = true
    )
    public String newPassword;

    @NotEmpty
    @ApiModelProperty(value = "새 비밀번호 확인", example = "NewPass123!", required = true)
    public String confirmPassword;
}
