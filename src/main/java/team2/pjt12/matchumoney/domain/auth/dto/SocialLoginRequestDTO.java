package team2.pjt12.matchumoney.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class SocialLoginRequestDTO {

    private String code;

    @JsonCreator
    public SocialLoginRequestDTO(@JsonProperty("code") String code) {
        this.code = code;
    }
}