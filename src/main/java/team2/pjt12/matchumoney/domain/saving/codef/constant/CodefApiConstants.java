package team2.pjt12.matchumoney.domain.saving.codef.constant;

public final class CodefApiConstants {
    // ===== 계정 CRUD (Connected ID/계정) =====
    public static final String ACCOUNT_CREATE_URL = "/v1/account/create";
    public static final String ACCOUNT_ADD_URL = "/v1/account/add";
    public static final String ACCOUNT_UPDATE_URL = "/v1/account/update";
    public static final String ACCOUNT_DELETE_URL = "/v1/account/delete";
    public static final String ACCOUNT_GET_URL = "/v1/account/list";              // 계정 목록
    public static final String ACCOUNT_GET_ID_URL = "/v1/account/connectedId-list";  // Connected ID 목록
    // ===== 은행 계좌/거래 =====
    // 프로젝트에서 실제 사용하는 상품 경로로 맞추되, 반드시 "경로만" 유지할 것
    public static final String ACCOUNT_LIST_URL = "/v1/kr/bank/p/account/account-list";
    public static final String TRANSACTION_LIST_URL = "/v1/kr/bank/p/installment-savings/transaction-list";
    // ===== 공통 코드 =====
    public static final String COUNTRY_CODE_KR = "KR";
    public static final String BUSINESS_TYPE_BANK = "BK";
    public static final String CLIENT_TYPE_PERSONAL = "P";
    public static final String LOGIN_TYPE_ID_PASSWORD = "1";
    // ===== 예/적금 구분 코드(업무 로직에 사용 시) =====
    public static final String DEPOSIT_TYPE_DEPOSIT = "11"; // 예금
    public static final String DEPOSIT_TYPE_SAVINGS = "12"; // 적금
    public static final String DEPOSIT_TYPE_SAVINGS2 = "14"; // 적금(기관별 추가 코드)
    // ===== 기본 조회 시작일 =====
    public static final String DEFAULT_START_DATE = "20011203"; // 필요 시 도메인 정책에 맞게 조정

    private CodefApiConstants() {
    }
}
