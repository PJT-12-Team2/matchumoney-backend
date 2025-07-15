package team2.pjt12.matchumoney.domain.auth.dto.req;

import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
public final class SendEmailRequestDTO {

    @NotEmpty
    @Email
    private final String email;

    public SendEmailRequestDTO(String email) {
        this.email = email;
    }
}