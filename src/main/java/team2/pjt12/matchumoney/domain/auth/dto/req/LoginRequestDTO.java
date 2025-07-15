package team2.pjt12.matchumoney.domain.auth.dto.req;


import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
public class LoginRequestDTO {

    @NotEmpty
    @Email
    private final String email;

    @NotEmpty
    private final String password;

    @Builder
    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}