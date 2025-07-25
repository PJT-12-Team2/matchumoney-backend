package team2.pjt12.matchumoney.domain.saving.codef;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefAccountRetrievalService {

    private final CodefApiClient codefApiClient;

    //계좌 목록 조회
    public List<JsonNode> retrieveAccountList(String accessToken, String connectedId, String orgCode) {
        try {
            String payload = buildAccountListPayload(connectedId, orgCode);

            log.info("계좌 목록 조회 요청 - 은행코드: {}", orgCode);

            JsonNode response = codefApiClient.postJson(CodefApiConstants.ACCOUNT_LIST_URL, accessToken, payload);
            JsonNode accountList = response.path("data").path("resDepositTrust");

            if (!accountList.isArray()) {
                log.warn("계좌 목록이 배열 형태가 아님 - 응답: {}", response.toPrettyString());
                return new ArrayList<>();
            }

            List<JsonNode> savingAccounts = new ArrayList<>();
            for (JsonNode account : accountList) {
                String depositType = account.path("resAccountDeposit").asText();
                if (CodefApiConstants.DEPOSIT_TYPE_SAVINGS.equals(depositType)) {
                    savingAccounts.add(account);
                }
            }






            log.info("✅ 적금 계좌 {}개 조회 완료", savingAccounts.size());
            return savingAccounts;

        } catch (Exception e) {
            log.error("계좌 목록 조회 중 예외 발생", e);
            throw new RuntimeException("계좌 목록 조회 실패", e);
        }
    }

    //거래 내역 조회
    public JsonNode retrieveTransactionHistory(String accessToken, String connectedId, String orgCode, String accountNumber) {
        try {
            String payload = buildTransactionPayload(connectedId, orgCode, accountNumber);

            log.info("거래내역 조회 요청 - 계좌번호: {}", accountNumber);

            JsonNode response = codefApiClient.postJson(CodefApiConstants.TRANSACTION_LIST_URL, accessToken, payload);
            JsonNode data = response.path("data");

            if (data == null || data.isEmpty()) {
                log.warn("거래내역이 존재하지 않음 - 계좌번호: {}", accountNumber);
                return null;
            }

            log.info("✅ 거래내역 조회 완료 - 계좌번호: {}", accountNumber);
            return data;

        } catch (Exception e) {
            log.error("거래내역 조회 중 예외 발생 - 계좌번호: {}", accountNumber, e);
            throw new RuntimeException("거래내역 조회 실패", e);
        }
    }

    private String buildAccountListPayload(String connectedId, String orgCode) {
        return String.format("""
            {
              "connectedId": "%s",
              "organization": "%s"
            }
            """, connectedId, orgCode);
    }

    private String buildTransactionPayload(String connectedId, String orgCode, String accountNumber) {
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
              "birthDate": ""
            }
            """, connectedId, orgCode, accountNumber, CodefApiConstants.DEFAULT_START_DATE, endDate);
    }
}