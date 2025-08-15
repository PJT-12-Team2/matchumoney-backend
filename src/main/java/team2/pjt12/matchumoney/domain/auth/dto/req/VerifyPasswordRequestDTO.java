// src/main/java/team2/pjt12/matchumoney/domain/auth/dto/req/VerifyPasswordRequestDTO.java
package team2.pjt12.matchumoney.domain.auth.dto.req;

import lombok.Getter;
import javax.validation.constraints.NotBlank;

@Getter
public class VerifyPasswordRequestDTO {
    @NotBlank
    private String password;
}
