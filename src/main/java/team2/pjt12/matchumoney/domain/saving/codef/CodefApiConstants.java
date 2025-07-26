package team2.pjt12.matchumoney.domain.saving.codef;


public final class CodefApiConstants {

    private CodefApiConstants() {}

    // 은행 코드(추후 확장을 위한 값)
    public static final String ORG_CODE_KB = "0004";         // 국민은행
    public static final String ORG_CODE_NONGHYUP = "0011";   // 농협은행
    public static final String ORG_CODE_WOORI = "0020";      // 우리은행

    // API URL
    public static final String CONNECTED_ID_URL = "https://development.codef.io/v1/account/create";
    public static final String ACCOUNT_LIST_URL = "https://development.codef.io/v1/kr/bank/p/account/account-list";
    public static final String TRANSACTION_LIST_URL = "https://development.codef.io/v1/kr/bank/p/installment-savings/transaction-list";

    // 계좌 타입
    public static final String DEPOSIT_TYPE_SAVINGS = "12";  // 적금
    public static final String DEPOSIT_TYPE_DEPOSIT = "11";  // 예금

    // 기본값
    public static final String DEFAULT_START_DATE = "20011203"; //적절한 과거로 설정(불가한 과거일시 가능한 경우로 변환)
    public static final String COUNTRY_CODE_KR = "KR";
    public static final String BUSINESS_TYPE_BANK = "BK";
    public static final String CLIENT_TYPE_PERSONAL = "P";
    public static final String LOGIN_TYPE_ID_PASSWORD = "1";
}