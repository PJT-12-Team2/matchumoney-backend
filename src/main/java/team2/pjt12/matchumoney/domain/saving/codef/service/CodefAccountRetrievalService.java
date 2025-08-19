package team2.pjt12.matchumoney.domain.saving.codef.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.saving.codef.CodefApiClient;
import team2.pjt12.matchumoney.domain.saving.codef.CodefConnectedIdProvider;
import team2.pjt12.matchumoney.domain.saving.codef.constant.CodefApiConstants;
import team2.pjt12.matchumoney.domain.saving.codef.mapper.CodefMapper;
import team2.pjt12.matchumoney.domain.saving.domain.DepositAccountVO;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.mapper.SavingAccountMapper;
import team2.pjt12.matchumoney.domain.saving.util.SavingAccountConverter;
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
    private final CodefMapper codefMapper;
    private final SavingAccountMapper savingAccountMapper;
    private final CodefConnectedIdProvider codefConnectedIdProvider;
    private final SavingAccountConverter dataTransformService;

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
        savingAccountMapper.deleteDepositByUserIdAndFinId(userId, Long.valueOf(orgCode));
        //적금 객체 반환
        List<JsonNode> savingAccounts = new ArrayList<>();
        log.info(accountList.toPrettyString());
        for (JsonNode account : accountList) {
            //종류
            String depositType = account.path("resAccountDeposit").asText();
            if (CodefApiConstants.DEPOSIT_TYPE_SAVINGS.equals(depositType) || CodefApiConstants.DEPOSIT_TYPE_SAVINGS2.equals(depositType)) {
                savingAccounts.add(account);
            } else if (CodefApiConstants.DEPOSIT_TYPE_DEPOSIT.equals(depositType)) {
                DepositAccountVO depositAccountVO = new DepositAccountVO(account, userId, Long.valueOf(orgCode));
                savingAccountMapper.insertDepositAccount(depositAccountVO);
            }
        }
        log.info("✅ 적금 계좌 {}개 조회 완료", savingAccounts.size());
        return savingAccounts;
    }

    //각 적금 세부 내역 조회
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

    //ACCESS TOKEN에 대한 CONNECTED ID
    public JsonNode getConnectedIdList() {
        String accessToken = codefApiClient.getAccessToken();
        String payload = """
                {
                  "pageNo": "0"
                }
                """;


        JsonNode response = codefApiClient.postJson(CodefApiConstants.ACCOUNT_GET_ID_URL, accessToken, payload);

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

    //connected id에 연결된 것들 목록
    public List<String> getOrganizationCodes(String connectedId) {
        String accessToken = codefApiClient.getAccessToken();

        String payload = String.format("""
                {
                  "connectedId": "%s"
                }
                """, connectedId);
        JsonNode response = codefApiClient.postJson(CodefApiConstants.ACCOUNT_GET_URL, accessToken, payload);
        log.info("accountList: " + response.toPrettyString());
        // 성공 여부 확인
        String resultCode = response.path("result").path("code").asText();
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

        // accountList에서 organization 추출
        List<String> organizationCodes = new ArrayList<>();
        JsonNode accountList = response.path("data").path("accountList");
        if (accountList != null && accountList.isArray()) {
            for (JsonNode account : accountList) {
                String orgCode = account.path("organization").asText(null);
                if (orgCode != null && !orgCode.isBlank()) {
                    organizationCodes.add(orgCode);
                }
            }
        }

        return organizationCodes;
    }

    //connected id 제거
    public void deleteConnectedId(String connectedId) {
        if (connectedId == null || connectedId.isBlank()) {
            throw new IllegalArgumentException("connectedId must not be null or blank");
        }

        final String accessToken = codefApiClient.getAccessToken();

        // 1) account/get
        final String getPayload = String.format("{\"connectedId\":\"%s\"}", connectedId);
        final JsonNode getResp = codefApiClient.postJson(CodefApiConstants.ACCOUNT_GET_URL, accessToken, getPayload);
        log.info("accountGet response: {}", getResp.toPrettyString());

        final String getCode = getResp.path("result").path("code").asText(null);
        final String getMsg = getResp.path("result").path("message").asText(null);
        if (!"CF-00000".equals(getCode)) {
            throw new CodefApiException(getCode, getMsg, List.of());
        }

        final JsonNode dataNode = getResp.path("data");
        if (dataNode == null || dataNode.isMissingNode() || dataNode.isNull()) {
            throw new CodefApiException("CF-DELETE-PAYLOAD-NOTFOUND",
                    "조회 응답의 data가 없어 삭제 페이로드를 구성할 수 없습니다.", List.of());
        }

        // 2) deletePayload 직접 생성 (필수 키만 포함)
        final String deletePayload = buildDeletePayloadFromGetData(dataNode);
        log.info("accountDelete request: {}", deletePayload);

        // 3) account/delete
        final JsonNode deleteResp =
                codefApiClient.postJson(CodefApiConstants.ACCOUNT_DELETE_URL, accessToken, deletePayload);

        log.info("accountDelete response: {}", deleteResp.toPrettyString());

        final String delCode = deleteResp.path("result").path("code").asText(null);
        final String delMsg = deleteResp.path("result").path("message").asText(null);
        if (!"CF-00000".equals(delCode)) {
            throw new CodefApiException(delCode, delMsg, List.of());
        }
    }

    //은행 로그인 정보 업데이트
    public List<MySavingProductResponseDTO> updateConnectedId(BankLoginRequestDTO requestDto) {
        Long userId = getCurrentUser().getUserId();

        // 1) 현재 사용자 connectedId 조회
        String connectedId = codefMapper.getCodefConnectedIdByUserId(userId);
        if (connectedId == null || connectedId.isBlank()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND); // 혹은 전용 에러코드
        }

        // 1. Access Token 발급
        String accessToken = codefApiClient.getAccessToken();

        try {
            codefConnectedIdProvider.updateAccountByConnectedId(
                    accessToken,
                    requestDto.getId(),
                    requestDto.getPassword(),
                    requestDto.getBankCode(),
                    requestDto.getBirthDate(),
                    connectedId
            );
        } catch (CustomException e) {
            log.error("계정 추가 실패 - {}", e.getErrorCode().getMessage());
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        // 4. 동기화된 계좌 목록 반환
        List<MySavingProductResponseDTO> result = savingAccountMapper.getSavingAccountList(userId);
        log.info("✅ 계좌 동기화 완료 - {}개 계좌", result.size());

        return result;
    }

    /**
     * account/get의 data에서 가이드에 맞는 최소 JSON으로 deletePayload를 조립한다.
     * 루트: connectedId, accountList
     * accountList[*]: countryCode, businessType, clientType, organization, loginType (모두 문자열)
     */
    private String buildDeletePayloadFromGetData(JsonNode dataNode) {
        final String cid = dataNode.path("connectedId").asText("");
        if (cid.isEmpty()) {
            throw new CodefApiException("CF-DELETE-PAYLOAD-BAD-DATA",
                    "data.connectedId가 비어있습니다.", List.of());
        }

        // accountList
        JsonNode srcList = dataNode.path("accountList");
        // 비어있어도 스펙상 빈 배열 허용
        StringBuilder sb = new StringBuilder(256);
        sb.append("{\"connectedId\":\"").append(escapeJson(cid)).append("\",\"accountList\":[");

        if (srcList != null && srcList.isArray() && srcList.size() > 0) {
            for (int i = 0; i < srcList.size(); i++) {
                JsonNode acc = srcList.get(i);

                // 허용된 5개 키만 추출(문자열 보장)
                String countryCode = textOrDefault(acc, "countryCode", "KR");
                String businessType = requiredText(acc, "businessType", "businessType");
                String clientType = requiredText(acc, "clientType", "clientType");
                String organization = requiredText(acc, "organization", "organization");
                String loginType = requiredText(acc, "loginType", "loginType"); // "0" 또는 "1"

                if (i > 0) sb.append(',');

                sb.append('{')
                        .append("\"countryCode\":\"").append(escapeJson(countryCode)).append('"').append(',')
                        .append("\"businessType\":\"").append(escapeJson(businessType)).append('"').append(',')
                        .append("\"clientType\":\"").append(escapeJson(clientType)).append('"').append(',')
                        .append("\"organization\":\"").append(escapeJson(organization)).append('"').append(',')
                        .append("\"loginType\":\"").append(escapeJson(loginType)).append('"')
                        .append('}');
            }
        }

        sb.append("]}");
        return sb.toString();
    }

    private String textOrDefault(JsonNode node, String field, String def) {
        JsonNode v = node.path(field);
        String s = (v.isMissingNode() || v.isNull()) ? null : v.asText(null);
        return (s == null || s.isEmpty()) ? def : s;
    }

    private String requiredText(JsonNode node, String field, String labelForError) {
        JsonNode v = node.path(field);
        String s = (v.isMissingNode() || v.isNull()) ? null : v.asText(null);
        if (s == null || s.isEmpty()) {
            throw new CodefApiException("CF-DELETE-PAYLOAD-BAD-DATA",
                    labelForError + " 값이 누락되었습니다.", List.of());
        }
        return s;
    }

    /**
     * 아주 단순한 JSON 문자열 이스케이프 (따옴표/역슬래시)
     */
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
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

    //계좌 동기화 처리
    @Transactional(rollbackFor = Exception.class)
    public void processAccountSynchronization(String accessToken, String connectedId, Long userId, String birthDate, String bankCode) {
        Long finId;
        try {
            finId = Long.parseLong(bankCode);
        } catch (NumberFormatException e) {
            log.error("금융기관 코드 파싱 실패: {}", bankCode);
            throw new CustomException(ErrorCode.DATA_CONVERSION_FAILED);
        }

        try {
            savingAccountMapper.deleteByUserIdAndFinId(userId, finId);
            log.info("기존 적금 계좌 삭제 완료 - 사용자ID: {}, 금융기관ID: {}", userId, finId);
        } catch (Exception e) {
            log.error("적금 계좌 삭제 실패", e);
            throw new CustomException(ErrorCode.DB_SAVING_DELETE_FAILED);
        }

        List<JsonNode> savingAccounts;
        try {
            savingAccounts = retrieveAccountList(
                    accessToken, connectedId, bankCode
            );
        } catch (CodefApiException e) {
            log.error("Codef API 실패 - code: {}, message: {}", e.getCode(), e.getMessage());
            throw e; // GlobalExceptionHandler에서 응답 내려감
        }

        int processedCount = 0;
        List<String> failedAccounts = new ArrayList<>();

        for (JsonNode account : savingAccounts) {
            String accountNumber = account.path("resAccount").asText();

            try {
                if (processTransactionHistory(accessToken, connectedId, accountNumber, userId, finId, birthDate, bankCode)) {
                    processedCount++;
                } else {
                    failedAccounts.add(accountNumber);
                }
            } catch (Exception e) {
                log.warn("거래내역 처리 실패 - accountNumber: {}, error: {}", accountNumber, e.getMessage());
                failedAccounts.add(accountNumber);
            }
        }

        log.info("✅ 계좌 처리 완료 - 총 {}개 중 {}개 성공", savingAccounts.size(), processedCount);

        if (!failedAccounts.isEmpty()) {
            log.warn("❗ 실패한 계좌 목록: {}", failedAccounts);
            // 경우에 따라 프론트에 실패 목록까지 전달하는 DTO도 구성 가능
        }
    }

    //거래내역 처리
    public boolean processTransactionHistory(String accessToken, String connectedId,
                                             String accountNumber, Long userId, Long finId, String birthDate, String bankCode) {

        JsonNode transactionData = retrieveTransactionHistory(
                accessToken, connectedId, bankCode, accountNumber, birthDate
        );

        SavingAccountVO vo = dataTransformService.transformToVO(transactionData, userId, finId);

        try {
            savingAccountMapper.insertSavingAccount(vo);
        } catch (Exception e) {
            log.error("적금 계좌 저장 실패 - 계좌번호: {}", accountNumber, e);
            throw new CustomException(ErrorCode.DB_SAVING_INSERT_FAILED);
        }
        log.info("✅ 적금 계좌 저장 완료 - 계좌번호: {}", accountNumber);
        return true;
    }


    public List<String> getBanksByConnectedId() {
        Long userId = getCurrentUser().getUserId();
        log.info("🏦 은행 계좌 동기화 시작 - 사용자ID: {}", userId);

        List<String> result = codefMapper.selectOrganizationNamesByUserId(userId);


        return result;
    }
}
