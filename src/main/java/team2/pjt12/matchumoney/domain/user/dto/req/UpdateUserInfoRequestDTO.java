package team2.pjt12.matchumoney.domain.user.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import team2.pjt12.matchumoney.domain.user.domain.Gender;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@ApiModel(value = "UpdateUserInfoRequest", description = "사용자 정보 수정 요청 DTO")
public class UpdateUserInfoRequestDTO {

    @NotEmpty
    @ApiModelProperty(value = "닉네임", example = "홍길동", required = true)
    public String nickname;

    @ApiModelProperty(value = "성별", example = "MALE")
    public Gender gender;

    @ApiModelProperty(value = "생년월일", example = "1990-01-01")
    public LocalDate birthDate;

    @ApiModelProperty(value = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    public String profileImageUrl;

}
