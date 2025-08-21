package team2.pjt12.matchumoney.domain.saving.codef;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.saving.codef.constant.CodefApiConstants;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.util.RsaEncryptor;
import team2.pjt12.matchumoney.global.config.CodefConfig;
import team2.pjt12.matchumoney.global.exception.CodefApiException;
import team2.pjt12.matchumoney.global.exception.CodefApiSubError;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefConnectedIdProvider {

    private final CodefApiClient codefApiClient;
    private final CodefConfig config;

    /**
     * Connected ID 생성
     */
    public String createConnectedId(String bankId, String password, String orgCode, String birthDate) {
        log.info("Connected ID 생성 요청 - 기관코드: {}, 사용자ID: {}", orgCode, bankId);

        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());
        Map<String, Object> payloadMap = buildBasePayloadMap(bankId, encryptedPassword, orgCode, birthDate);
        log.info("payload" + payloadMap);

        try {
            JsonNode response = codefApiClient.postJson(
                    CodefApiConstants.ACCOUNT_CREATE_URL,
                    new HashMap<>(payloadMap)
            );
            handleApiResponse(response, "Connected ID 생성");

            String connectedId = response.path("data").path("connectedId").asText(null);
            if (connectedId == null || connectedId.isBlank()) {
                throw new CustomException(ErrorCode.CODEF_LOGIN);
            }
            return connectedId;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Connected ID 생성 중 예외", e);
            throw new CustomException(ErrorCode.CODEF_ERROR);
        }
    }

    /**
     * 기존 Connected ID에 계정 추가
     */
    public String addAccountByConnectedId(String bankId, String password, String orgCode, String birthDate, String connectedId) {
        log.info("계정 추가 요청 - 기관코드: {}, 사용자ID: {}", orgCode, bankId);
        return executeAccountModification(bankId, password, orgCode, birthDate, connectedId,
                CodefApiConstants.ACCOUNT_ADD_URL, "계정 추가");
    }

    /**
     * Connected ID 계정 정보 수정
     */
    public String updateAccountByConnectedId(BankLoginRequestDTO requestDto, String connectedId) {
        log.info("계정 수정 요청 - 기관코드: {}, 사용자ID: {}", requestDto.getBankCode(), requestDto.getId());
        return executeAccountModification(requestDto.getId(), requestDto.getPassword(), requestDto.getBankCode(), requestDto.getBirthDate(), connectedId,
                CodefApiConstants.ACCOUNT_UPDATE_URL, "계정 수정");
    }

    private String executeAccountModification(String bankId, String password, String orgCode, String birthDate,
                                              String connectedId, String apiUrl, String operationName) {
        if (connectedId == null || connectedId.isBlank()) {
            log.warn("{} 요청에 connectedId가 비어 있습니다.", operationName);
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());

        Map<String, Object> account = new LinkedHashMap<>();
        account.put("countryCode", "KR");
        account.put("businessType", "BK");
        account.put("clientType", "P");
        account.put("organization", orgCode);
        account.put("loginType", "1");
        account.put("id", bankId);
        account.put("password", encryptedPassword);
        if (birthDate != null && !birthDate.isBlank()) account.put("birthDate", birthDate);
        account.put("loginTypeLevel", "");
        account.put("clientTypeLevel", "");
        account.put("cardNo", "");
        account.put("cardPassword", "");

        List<Map<String, Object>> accountList = new ArrayList<>();
        accountList.add(account);

        HashMap<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("accountList", accountList);
        payloadMap.put("connectedId", connectedId);
//        log.info("add: " + payloadMap);
        try {
            JsonNode response = codefApiClient.postJson(apiUrl, payloadMap);
            handleApiResponse(response, operationName);
            return operationName + "_SUCCESS";
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("{} 중 예외", operationName, e);
            throw new CustomException(ErrorCode.CODEF_ERROR);
        }
    }

    /**
     * 공통 페이로드 구성
     */
    private Map<String, Object> buildBasePayloadMap(String bankId, String encryptedPassword, String orgCode, String birthDate) {
        Map<String, Object> account = new LinkedHashMap<>();
        account.put("countryCode", "KR");
        account.put("businessType", "BK");
        account.put("clientType", "P");
        account.put("organization", orgCode);
        account.put("loginType", "1");
        account.put("id", bankId);
        account.put("password", encryptedPassword);
        if (birthDate != null && !birthDate.isBlank()) account.put("birthDate", birthDate);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("accountList", List.of(account));
        return body;
    }

    /**
     * 공통 응답 처리
     */
    private void handleApiResponse(JsonNode response, String operationName) {
        String resultCode = response.path("result").path("code").asText();
        if (!"CF-00000".equals(resultCode)) {
            String message = response.path("result").path("message").asText();
            List<CodefApiSubError> subErrorList = new ArrayList<>();
            JsonNode errorList = response.path("data").path("errorList");
            if (errorList.isArray()) {
                for (JsonNode err : errorList) {
                    subErrorList.add(new CodefApiSubError(err.path("code").asText(), err.path("message").asText()));
                }
            }
            log.error("{} 실패 - code: {}, message: {}", operationName, resultCode, message);
            throw new CodefApiException(resultCode, message, subErrorList);
        }
        JsonNode errorListNode = response.path("data").path("errorList");
        if (errorListNode.isArray() && !errorListNode.isEmpty()) {
            log.warn("{} 작업에 부분 실패가 포함되었습니다.", operationName);
            throw new CustomException(ErrorCode.CODEF_LOGIN);
        }
    }
}
