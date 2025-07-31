package team2.pjt12.matchumoney.global.exception;


import lombok.Getter;

import java.util.List;

@Getter
public class CodefApiException extends RuntimeException {
    private final String code;
    private final String message;
    private final List<CodefApiSubError> subErrorList;

    public CodefApiException(String code, String message,
                             List<CodefApiSubError> subErrorList) {
        super(message);
        this.code = code;
        this.message = message;
        this.subErrorList = subErrorList;
    }
}
