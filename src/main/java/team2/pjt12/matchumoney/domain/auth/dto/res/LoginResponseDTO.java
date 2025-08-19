package team2.pjt12.matchumoney.domain.auth.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(description = "로그인 응답 DTO")
public class LoginResponseDTO {

    @ApiModelProperty(value = "JWT 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private final String accessToken;

    @ApiModelProperty(value = "사용자 고유 ID", example = "1")
    private final Long userId;

    @ApiModelProperty(value = "사용자 닉네임", example = "머니머니")
    private final String nickname;

    @ApiModelProperty(value = "사용자 페르소나 ID", example = "1")
    private final Long personaId;
}