package team2.pjt12.matchumoney.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 공통 예외
    INVALID_INPUT_VALUE(400, "C400", "비밀번호의 형식이 맞지 않습니다."),
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
    NOT_MATCH_PASSWORD(400, "U400", "비밀번호가 일치하지 않습니다."),
    USER_ALREADY_EXISTS(409, "U409", "이미 존재하는 사용자입니다."),
    PERSONA_NOT_FOUND(404, "P404", "해당 페르소나를 찾을 수 없습니다."),

    // 인증 관련 예외
    INVALID_PASSWORD(401, "A401", "현재 비밀번호가 일치하지 않습니다."),
    SAME_PASSWORD(400, "A400", "현재 비밀번호와 새 비밀번호가 동일합니다."),
    PASSWORD_MISMATCH(400, "A400", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다."),

    // 즐겨찾기 관련 예외
    FAVORITE_ALREADY_EXISTS(409, "F409", "이미 존재하는 즐겨찾기입니다."),

    // 상품 관련 예외
    INVALID_PRODUCT_TYPE(400, "P400", "유효하지 않은 상품 유형입니다."),

    // codef 관련 에러(적금)
    CODEF_ERROR(401, "F401", "CODEF에 접근할 수 없습니다."),
    CODEF_LOGIN(400, "F400", "유효하지 않은 아이디/비밀번호입니다."),
    RSA_ENCRYPTION_FAIL(500, "C500", "암호화 중 오류  발생했습니다."),
    DATA_CONVERSION_FAILED(401, "C401", "데이터 변환에 실패했습니다."),
    DB_SAVING_INSERT_FAILED(500, "S500", "적금 계좌 저장 중 오류가 발생했습니다."),
    DB_SAVING_DELETE_FAILED(500, "S500", "적금 계좌 삭제 중 오류가 발생했습니다."),
    CODEF_TIMEOUT(504, "C504", "CODEF API 응답이 지연되었습니다."),
    CODEF_RESPONSE_PARSE_ERROR(500, "C505", "CODEF 응답 파싱 중 오류 발생"),
    CODEF_COMMUNICATION_ERROR(502, "C502", "CODEF API 통신 오류"),
    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
