package team2.pjt12.matchumoney.domain.auth.dto.req;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
public final class VerifyEmailRequestDTO {

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String code;

    public VerifyEmailRequestDTO(String email, String code) {
        this.email = email;
        this.code = code;
    }
}