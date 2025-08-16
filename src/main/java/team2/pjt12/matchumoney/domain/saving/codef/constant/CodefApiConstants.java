package team2.pjt12.matchumoney.domain.saving.codef.constant;


public final class CodefApiConstants {

    // API URL
    public static final String ACCOUNT_LIST_URL = "https://development.codef.io/v1/kr/bank/p/account/account-list";
    public static final String TRANSACTION_LIST_URL = "https://development.codef.io/v1/kr/bank/p/installment-savings/transaction-list";

    // 계정 CRUD 관련 API URL
    public static final String ACCOUNT_CREATE_URL = "https://development.codef.io/v1/account/create";
    public static final String ACCOUNT_ADD_URL = "https://development.codef.io/v1/account/add";
    public static final String ACCOUNT_DELETE_URL = "https://development.codef.io/v1/account/delete";
    public static final String ACCOUNT_GET_URL = "https://development.codef.io/v1/account/list";
    public static final String ACCOUNT_GET_ID_URL = "https://development.codef.io/v1/account/connectedId-list";
    public static final String ACCOUNT_UPDATE_URL = "https://development.codef.io/v1/account/update";

    // 계좌 타입
    public static final String DEPOSIT_TYPE_SAVINGS = "12";  // 적금
    public static final String DEPOSIT_TYPE_SAVINGS2 = "14";  // 적금
    public static final String DEPOSIT_TYPE_DEPOSIT = "11";  // 예금

    // 기본값
    public static final String DEFAULT_START_DATE = "20011203"; //적절한 과거로 설정(불가한 과거일시 가능한 경우로 변환)
    public static final String COUNTRY_CODE_KR = "KR";
    public static final String BUSINESS_TYPE_BANK = "BK";
    public static final String CLIENT_TYPE_PERSONAL = "P";
    public static final String LOGIN_TYPE_ID_PASSWORD = "1";
}