package team2.pjt12.matchumoney.domain.auth.dto.req;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class VerifyEmailRequestDTO {

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String code;
}