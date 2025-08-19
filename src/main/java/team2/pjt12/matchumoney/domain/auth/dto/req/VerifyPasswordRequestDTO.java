// src/main/java/team2/pjt12/matchumoney/domain/auth/dto/req/VerifyPasswordRequestDTO.java
package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@ApiModel(description = "비밀번호 검증 요청 DTO")
public class VerifyPasswordRequestDTO {
    @ApiModelProperty(value = "현재 비밀번호", example = "P@ssw0rd!", required = true)
    @JsonAlias({"password", "raw_password"})  // ✅ password도 허용
    private String rawPassword;
}
