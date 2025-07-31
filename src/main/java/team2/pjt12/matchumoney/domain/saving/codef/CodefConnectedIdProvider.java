package team2.pjt12.matchumoney.domain.saving.codef;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.saving.util.RsaEncryptor;
import team2.pjt12.matchumoney.global.config.CodefConfig;
import team2.pjt12.matchumoney.global.exception.CodefApiException;
import team2.pjt12.matchumoney.global.exception.CodefApiSubError;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefConnectedIdProvider {

    private final CodefApiClient codefApiClient;
    private final CodefConfig config;

    /**
     * CODEF Connected ID 생성
     */
    public String createConnectedId(String accessToken, String bankId, String password, String orgCode, String birthDate) throws JsonProcessingException {
        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());
        String payload = buildConnectedIdPayload(bankId, encryptedPassword, orgCode, birthDate);

        log.info("Connected ID 생성 요청 - 은행코드: {}, 사용자ID: {}", orgCode, bankId);

        JsonNode response = codefApiClient.postJson(CodefApiConstants.CONNECTED_ID_URL, accessToken, payload);

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

        //log.info("✅ Connected ID 생성 성공 - connectedId: {}", connectedId);
        return connectedId;
    }


    private String buildConnectedIdPayload(String bankId, String encryptedPassword, String orgCode, String birthDate)
            throws JsonProcessingException {

        Map<String, Object> account = new HashMap<>();
        account.put("countryCode", CodefApiConstants.COUNTRY_CODE_KR);
        account.put("businessType", CodefApiConstants.BUSINESS_TYPE_BANK);
        account.put("clientType", CodefApiConstants.CLIENT_TYPE_PERSONAL);
        account.put("organization", orgCode);
        account.put("loginType", CodefApiConstants.LOGIN_TYPE_ID_PASSWORD);
        account.put("id", bankId);
        account.put("password", encryptedPassword);

        // 생년월일이 필요한 경우만 추가
        if (birthDate != null && !birthDate.isBlank()) {
            account.put("birthDate", birthDate);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("accountList", List.of(account));

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(body);
    }

}

