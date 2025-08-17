// src/main/java/team2/pjt12/matchumoney/domain/auth/dto/req/VerifyPasswordRequestDTO.java
package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class VerifyPasswordRequestDTO {
    @JsonAlias({"password", "raw_password"})  // ✅ password도 허용
    private String rawPassword;
}
