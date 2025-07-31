package team2.pjt12.matchumoney.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CodefApiSubError {
    private String code;
    private String message;
}
