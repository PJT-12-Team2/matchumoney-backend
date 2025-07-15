package team2.pjt12.matchumoney.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 공통 예외
    INVALID_INPUT_VALUE(400, "C400", "잘못된 입력입니다."),
    METHOD_NOT_ALLOWED(405, "C405", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(500, "C500", "서버 오류가 발생했습니다."),
    RESOURCE_NOT_FOUND(404, "C404", "요청한 리소스를 찾을 수 없습니다."),

    // 사용자 관련 예외
    USER_NOT_FOUND(404, "U404", "해당 사용자를 찾을 수 없습니다."),
    USER_DUPLICATE(409, "U409", "이미 존재하는 사용자입니다."),
    USER_NOT_AUTHORIZED(403, "U403", "유효하지 않은 사용자입니다."),
    EMAIL_NOT_FOUND(404, "U404", "해당 이메일을 가진 사용자를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED(403, "U403", "검증되지 않은 이메일입니다."),
    EMAIL_NOT_AVAILABLE(409, "U409", "사용할 수 없는 이메일입니다."),
    NOT_MATCH_PASSWORD(400, "U400", "비밀번호가 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
