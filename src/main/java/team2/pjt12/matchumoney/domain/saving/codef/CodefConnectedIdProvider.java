package team2.pjt12.matchumoney.domain.saving.codef;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.saving.codef.constant.CodefApiConstants;
import team2.pjt12.matchumoney.domain.saving.util.RsaEncryptor;
import team2.pjt12.matchumoney.global.config.CodefConfig;
import team2.pjt12.matchumoney.global.exception.CodefApiException;
import team2.pjt12.matchumoney.global.exception.CodefApiSubError;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefConnectedIdProvider {

    private final CodefApiClient codefApiClient;
    private final CodefConfig config;
    private final ObjectMapper mapper = new ObjectMapper(); // ObjectMapper는 재사용하는 것이 효율적입니다.

    /**
     * Connected ID 생성
     */
    public String createConnectedId(String accessToken, String bankId, String password, String orgCode, String birthDate) {
        log.info("Connected ID 생성 요청 - 기관코드: {}, 사용자ID: {}", orgCode, bankId);

        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());
        Map<String, Object> payloadMap = buildBasePayloadMap(bankId, encryptedPassword, orgCode, birthDate);
        try {
            String payload = mapper.writeValueAsString(payloadMap);
            JsonNode response = codefApiClient.postJson(CodefApiConstants.ACCOUNT_CREATE_URL, accessToken, payload);

            // 응답 처리
            handleApiResponse(response, "Connected ID 생성");
            String connectedId = response.path("data").path("connectedId").asText(null);
            if (connectedId == null || connectedId.isBlank()) {
                throw new CustomException(ErrorCode.CODEF_LOGIN);
            }
            return connectedId;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 기존 Connected ID에 계정 추가
     */
    public String addAccountByConnectedId(String accessToken, String bankId, String password, String orgCode, String birthDate, String connectedId) {
        log.info("계정 추가 요청 - 기관코드: {}, 사용자ID: {}", orgCode, bankId);
        return executeAccountModification(accessToken, bankId, password, orgCode, birthDate, connectedId, CodefApiConstants.ACCOUNT_ADD_URL, "계정 추가");
    }

    /**
     * Connected ID 계정 정보 수정
     */
    public String updateAccountByConnectedId(String accessToken, String bankId, String password, String orgCode, String birthDate, String connectedId) {
        log.info("계정 수정 요청 - 기관코드: {}, 사용자ID: {}", orgCode, bankId);
        return executeAccountModification(accessToken, bankId, password, orgCode, birthDate, connectedId, CodefApiConstants.ACCOUNT_UPDATE_URL, "계정 수정");
    }

    /**
     * [리팩토링] 계정 추가/수정을 위한 통합 실행 메소드
     */
    private String executeAccountModification(String accessToken, String bankId, String password, String orgCode, String birthDate, String connectedId, String apiUrl, String operationName) {
        if (connectedId == null || connectedId.isBlank()) {
            log.warn("{} 요청에 connectedId가 비어 있습니다.", operationName);
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());
        Map<String, Object> payloadMap = buildBasePayloadMap(bankId, encryptedPassword, orgCode, birthDate);
        payloadMap.put("connectedId", connectedId); // String으로 변환하기 전에 connectedId 추가

        try {
            String payload = mapper.writeValueAsString(payloadMap);
            JsonNode response = codefApiClient.postJson(apiUrl, accessToken, payload);

            handleApiResponse(response, operationName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return operationName + "_SUCCESS";
    }

    /**
     * [리팩토링] 공통 페이로드 Map 생성
     */
    private Map<String, Object> buildBasePayloadMap(String bankId, String encryptedPassword, String orgCode, String birthDate) {
        // LinkedHashMap을 사용하여 JSON Key 순서를 보장하는 것이 안전합니다.
        Map<String, Object> account = new LinkedHashMap<>();
        account.put("countryCode", CodefApiConstants.COUNTRY_CODE_KR);
        account.put("businessType", CodefApiConstants.BUSINESS_TYPE_BANK);
        account.put("clientType", CodefApiConstants.CLIENT_TYPE_PERSONAL);
        account.put("organization", orgCode);
        account.put("loginType", CodefApiConstants.LOGIN_TYPE_ID_PASSWORD);
        account.put("id", bankId);
        account.put("password", encryptedPassword);
        if (birthDate != null && !birthDate.isBlank()) {
            account.put("birthDate", birthDate);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("accountList", List.of(account));
        return body;
    }

    /**
     * [리팩토링] 공통 API 응답 처리
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

        // 부분 실패 처리 (에러 리스트가 내려오는 경우)
        JsonNode errorListNode = response.path("data").path("errorList");
        if (errorListNode.isArray() && !errorListNode.isEmpty()) {
            log.warn("{} 작업에 부분 실패가 포함되었습니다.", operationName);
            // 필요시 첫 번째 에러 메시지를 기반으로 예외 발생
            throw new CustomException(ErrorCode.CODEF_LOGIN);
        }
    }
}