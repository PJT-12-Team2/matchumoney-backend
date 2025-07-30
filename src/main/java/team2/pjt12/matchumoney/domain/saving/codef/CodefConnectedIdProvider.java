package team2.pjt12.matchumoney.domain.saving.codef;


import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefConnectedIdProvider {

    private final CodefApiClient codefApiClient;
    private final CodefConfig config;

    /**
     * CODEF Connected ID 생성
     */
    public String createConnectedId(String accessToken, String bankId, String password, String orgCode) {
        String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());
        String payload = buildConnectedIdPayload(bankId, encryptedPassword, orgCode);

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

        log.info("✅ Connected ID 생성 성공 - connectedId: {}", connectedId);
        return connectedId;
    }


    private String buildConnectedIdPayload(String bankId, String encryptedPassword, String orgCode) {
//        생일 여기에 추가(리스트 조회)
        return String.format("""
                        {
                          "accountList": [
                            {
                              "countryCode": "%s",
                              "businessType": "%s",
                              "clientType": "%s",
                              "organization": "%s",
                              "loginType": "%s",
                              "id": "%s",
                              "password": "%s",
                            "birthDate": "20011203"
                            }
                          ]
                        }
                        """,
                CodefApiConstants.COUNTRY_CODE_KR,
                CodefApiConstants.BUSINESS_TYPE_BANK,
                CodefApiConstants.CLIENT_TYPE_PERSONAL,
                orgCode,
                CodefApiConstants.LOGIN_TYPE_ID_PASSWORD,
                bankId,
                encryptedPassword
        );
    }
}