package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
public class SendEmailRequestDTO {

    @NotEmpty
    @Email
    private final String email;

    public SendEmailRequestDTO(
            @JsonProperty("email") String email) {
        this.email = email;
    }
}