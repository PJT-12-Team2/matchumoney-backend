package team2.pjt12.matchumoney.domain.auth.dto.req;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SocialLoginRequestDTO {

    private String code;

    @JsonCreator
    public SocialLoginRequestDTO(@JsonProperty("code") String code) {
        this.code = code;
    }
}