package team2.pjt12.matchumoney.domain.saving.codef;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    public String createConnectedId(String accessToken,
                                    String bankId,
                                    String password,
                                    String orgCode,
                                    String birthDate) throws JsonProcessingException {

        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());
        String payload = buildConnectedIdCreatePayload(bankId, encryptedPassword, orgCode, birthDate);

        log.info("Connected ID 생성 요청 - 기관코드: {}, 사용자ID: {}", orgCode, bankId);

        JsonNode response = codefApiClient.postJson(CodefApiConstants.ACCOUNT_CREATE_URL, accessToken, payload);

        String resultCode = response.path("result").path("code").asText();
        if (!"CF-00000".equals(resultCode)) {
            String message = response.path("result").path("message").asText();
            List<CodefApiSubError> subErrorList = new ArrayList<>();
            JsonNode errorList = response.path("data").path("errorList");
            if (errorList.isArray()) {
                for (JsonNode err : errorList) {
                    subErrorList.add(new CodefApiSubError(
                            err.path("code").asText(),
                            err.path("message").asText()
                    ));
                }
            }
            throw new CodefApiException(resultCode, message, subErrorList);
        }

        String connectedId = response.path("data").path("connectedId").asText(null);
        if (connectedId == null || connectedId.isBlank()) {
            throw new CustomException(ErrorCode.CODEF_LOGIN);
        }
        return connectedId;
    }

    /**
     * 기존 Connected ID에 계정 추가
     * 반환 문자열은 상태 메시지일 뿐이며, connectedId 재할당 용도로 사용하지 마세요.
     */
    public String addAccountByConnectedId(String accessToken,
                                          String bankId,
                                          String password,
                                          String orgCode,
                                          String birthDate,
                                          String connectedId) throws JsonProcessingException {

        if (connectedId == null || connectedId.isBlank()) {
            log.warn("connectedId가 비어 있습니다.");
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());

        // 추가용 payload 생성
        String payload = buildAccountAddPayload(bankId, encryptedPassword, orgCode, birthDate);

        // connectedId 주입
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.readTree(payload);
        root.put("connectedId", connectedId);
        payload = mapper.writeValueAsString(root);

        log.info("계정 추가 요청 - 기관코드: {}, 사용자ID: {}, connectedId 존재 여부: {}",
                orgCode, bankId, (connectedId != null));

        JsonNode response = codefApiClient.postJson(CodefApiConstants.ACCOUNT_ADD_URL, accessToken, payload);

        String resultCode = response.path("result").path("code").asText();
        if (!"CF-00000".equals(resultCode)) {
            String message = response.path("result").path("message").asText();
            List<CodefApiSubError> subErrorList = new ArrayList<>();
            JsonNode errorList = response.path("data").path("errorList");
            if (errorList.isArray()) {
                for (JsonNode err : errorList) {
                    subErrorList.add(new CodefApiSubError(
                            err.path("code").asText(),
                            err.path("message").asText()
                    ));
                }
            }
            throw new CodefApiException(resultCode, message, subErrorList);
        }

        // 하위 에러 리스트가 내려오는 경우(부분 실패 등) 별도 처리
        JsonNode errorListNode = response.path("data").path("errorList");
        if (errorListNode.isArray() && errorListNode.size() > 0) {
            throw new CustomException(ErrorCode.CODEF_LOGIN);
        }

        return "ACCOUNT_ADD_SUCCESS";
    }

    public String updateAccountByConnectedId(String accessToken,
                                             String bankId,
                                             String password,
                                             String orgCode,
                                             String birthDate,
                                             String connectedId) throws JsonProcessingException {

        if (connectedId == null || connectedId.isBlank()) {
            log.warn("connectedId가 비어 있습니다.");
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());

        // 추가용 payload 생성
        String payload = buildAccountAddPayload(bankId, encryptedPassword, orgCode, birthDate);

        // connectedId 주입
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = (ObjectNode) mapper.readTree(payload);
        root.put("connectedId", connectedId);
        payload = mapper.writeValueAsString(root);

        log.info("계정 추가 요청 - 기관코드: {}, 사용자ID: {}, connectedId 존재 여부: {}",
                orgCode, bankId, (connectedId != null));

        JsonNode response = codefApiClient.postJson(CodefApiConstants.ACCOUNT_UPDATE_URL, accessToken, payload);

        String resultCode = response.path("result").path("code").asText();
        if (!"CF-00000".equals(resultCode)) {
            String message = response.path("result").path("message").asText();
            List<CodefApiSubError> subErrorList = new ArrayList<>();
            JsonNode errorList = response.path("data").path("errorList");
            if (errorList.isArray()) {
                for (JsonNode err : errorList) {
                    subErrorList.add(new CodefApiSubError(
                            err.path("code").asText(),
                            err.path("message").asText()
                    ));
                }
            }
            throw new CodefApiException(resultCode, message, subErrorList);
        }

        // 하위 에러 리스트가 내려오는 경우(부분 실패 등) 별도 처리
        JsonNode errorListNode = response.path("data").path("errorList");
        if (errorListNode.isArray() && errorListNode.size() > 0) {
            throw new CustomException(ErrorCode.CODEF_LOGIN);
        }

        return "ACCOUNT_UPDATE_SUCCESS";
    }

    /**
     * Connected ID 생성용 페이로드
     * (계정 추가와 동일 스키마이지만 connectedId가 포함되지 않음)
     */
    private String buildConnectedIdCreatePayload(String bankId,
                                                 String encryptedPassword,
                                                 String orgCode,
                                                 String birthDate) throws JsonProcessingException {
        Map<String, Object> account = new HashMap<>();
        account.put("countryCode", CodefApiConstants.COUNTRY_CODE_KR);
        account.put("businessType", CodefApiConstants.BUSINESS_TYPE_BANK);  // BK
        account.put("clientType", CodefApiConstants.CLIENT_TYPE_PERSONAL);  // P
        account.put("organization", orgCode);
        account.put("loginType", CodefApiConstants.LOGIN_TYPE_ID_PASSWORD); // 1 (아이디/비번)
        account.put("id", bankId);
        account.put("password", encryptedPassword);
        if (birthDate != null && !birthDate.isBlank()) {
            account.put("birthDate", birthDate); // 아이디 방식에서 은행별로 필수인 경우가 많음
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("accountList", List.of(account));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(body);
    }

    /**
     * 계정 추가(account/add)용 페이로드
     */
    private String buildAccountAddPayload(String bankId,
                                          String encryptedPassword,
                                          String orgCode,
                                          String birthDate) throws JsonProcessingException {
        Map<String, Object> account = new LinkedHashMap<>();
        account.put("countryCode", CodefApiConstants.COUNTRY_CODE_KR);
        account.put("businessType", CodefApiConstants.BUSINESS_TYPE_BANK);  // BK
        account.put("clientType", CodefApiConstants.CLIENT_TYPE_PERSONAL);  // P (법인은 별도 상수 사용)
        account.put("organization", orgCode);
        account.put("loginType", CodefApiConstants.LOGIN_TYPE_ID_PASSWORD); // 1
        account.put("id", bankId);
        account.put("password", encryptedPassword);
        if (birthDate != null && !birthDate.isBlank()) {
            account.put("birthDate", birthDate);
        }
        // 기관 요구 시: account.put("isEncrypted", "Y"); 등 부가 필드 추가

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("accountList", List.of(account));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(body);
    }
}
