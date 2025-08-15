package team2.pjt12.matchumoney.domain.user.dto.req;

import team2.pjt12.matchumoney.domain.user.domain.Gender;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

public class UpdateUserInfoRequestDTO {

    @NotEmpty
    public String nickname;

    public Gender gender;

    public LocalDate birthDate;

    public String profileImageUrl;   // ← 추가
}
