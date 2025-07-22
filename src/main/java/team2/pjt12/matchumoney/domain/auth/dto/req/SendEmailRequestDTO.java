package team2.pjt12.matchumoney.domain.auth.dto.req;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Builder
public class SendEmailRequestDTO {

    @NotEmpty
    @Email
    private final String email;
}