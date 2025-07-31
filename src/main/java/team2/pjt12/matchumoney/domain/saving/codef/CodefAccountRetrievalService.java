package team2.pjt12.matchumoney.domain.saving.codef;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.saving.domain.DepositAccountVO;
import team2.pjt12.matchumoney.domain.saving.mapper.SavingAccountMapper;
import team2.pjt12.matchumoney.global.exception.CodefApiException;
import team2.pjt12.matchumoney.global.exception.CodefApiSubError;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefAccountRetrievalService {

    private final CodefApiClient codefApiClient;
    private final SavingAccountMapper savingAccountMapper;

    //계좌 목록 조회
    public List<JsonNode> retrieveAccountList(String accessToken, String connectedId, String orgCode) {

        String payload = buildAccountListPayload(connectedId, orgCode);

        log.info("계좌 목록 조회 요청 - 은행코드: {}", orgCode);

        JsonNode response = codefApiClient.postJson(CodefApiConstants.ACCOUNT_LIST_URL, accessToken, payload);

        String resultCode = response.path("result").path("code").asText();

        //성공하지 못하면
        if (!"CF-00000".equals(resultCode)) {
            String message = response.path("result").path("message").asText();

            List<CodefApiSubError> subErrorList = new ArrayList<>();
            JsonNode errorList = response.path("data").path("errorList");
            if (errorList != null && errorList.isArray()) {
                for (JsonNode err : errorList) {
                    subErrorList.add(new CodefApiSubError(
                            err.path("code").asText(),
                            err.path("message").asText()
                    ));
                }
            }
            throw new CodefApiException(resultCode, message, subErrorList);
        }

        JsonNode accountList = response.path("data").path("resDepositTrust");

        if (!accountList.isArray()) {
            log.warn("계좌 목록이 배열 형태가 아님 - 응답: {}", response.toPrettyString());
            return new ArrayList<>();
        }

        Long userId = getCurrentUser().getUserId();
        //예금 지워
        savingAccountMapper.deleteDepositByUserIdAndFinId(userId, Long.valueOf(CodefApiConstants.ORG_CODE_KB));
        //적금 객체 반환
        List<JsonNode> savingAccounts = new ArrayList<>();
        log.info(accountList.toPrettyString());
        for (JsonNode account : accountList) {
            //종류
            String depositType = account.path("resAccountDeposit").asText();
            if (CodefApiConstants.DEPOSIT_TYPE_SAVINGS.equals(depositType)) {
                savingAccounts.add(account);
            } else if (CodefApiConstants.DEPOSIT_TYPE_DEPOSIT.equals(depositType)) {
                DepositAccountVO depositAccountVO = new DepositAccountVO(account, userId, Long.valueOf(CodefApiConstants.ORG_CODE_KB));
                savingAccountMapper.insertDepositAccount(depositAccountVO);
            }
        }
        log.info("✅ 적금 계좌 {}개 조회 완료", savingAccounts.size());
        return savingAccounts;
    }

    //거래 내역 조회
    public JsonNode retrieveTransactionHistory(String accessToken, String connectedId, String orgCode, String accountNumber, String birthDate) {
        String payload = buildTransactionPayload(connectedId, orgCode, accountNumber, birthDate);

        log.info("거래내역 조회 요청 - 계좌번호: {}", accountNumber);
        JsonNode response = codefApiClient.postJson(CodefApiConstants.TRANSACTION_LIST_URL, accessToken, payload);

        String resultCode = response.path("result").path("code").asText();

        //성공하지 못하면
        if (!"CF-00000".equals(resultCode)) {
            String message = response.path("result").path("message").asText();

            List<CodefApiSubError> subErrorList = new ArrayList<>();
            JsonNode errorList = response.path("data").path("errorList");
            if (errorList != null && errorList.isArray()) {
                for (JsonNode err : errorList) {
                    subErrorList.add(new CodefApiSubError(
                            err.path("code").asText(),
                            err.path("message").asText()
                    ));
                }
            }
            throw new CodefApiException(resultCode, message, subErrorList);
        }

        JsonNode data = response.path("data");
        log.info(data.toPrettyString());
        if (data == null || data.isEmpty()) {
            log.warn("거래내역이 존재하지 않음 - 계좌번호: {}", accountNumber);
            throw new CustomException(ErrorCode.CODEF_ERROR);
        }

        log.info("✅ 거래내역 조회 완료 - 계좌번호: {}", accountNumber);
        return data;

    }

    private String buildAccountListPayload(String connectedId, String orgCode) {
        return String.format("""
                {
                  "connectedId": "%s",
                  "organization": "%s"
                }
                """, connectedId, orgCode);
    }

    //각 계좌 조회
    private String buildTransactionPayload(String connectedId, String orgCode, String accountNumber, String birthDate) {
        //오늘까지로 조회되도록(미래일 경우 오류 발생)
        String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return String.format("""
                {
                  "connectedId": "%s",
                  "organization": "%s",
                  "account": "%s",
                  "startDate": "%s",
                  "endDate": "%s",
                  "orderBy": "0",
                  "inquiryType": "1",
                  "birthDate": "%s"
                }
                """, connectedId, orgCode, accountNumber, CodefApiConstants.DEFAULT_START_DATE, endDate, birthDate);
    }
}
