package team2.pjt12.matchumoney.domain.user.dto.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public final class UserUpdateResponseDTO {

    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private final Long userId;

    public UserUpdateResponseDTO(Long userId) {
        this.userId = userId;
    }
}