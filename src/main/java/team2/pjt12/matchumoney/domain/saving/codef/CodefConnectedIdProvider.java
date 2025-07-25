package team2.pjt12.matchumoney.domain.saving.codef;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.saving.util.RsaEncryptor;
import team2.pjt12.matchumoney.global.config.CodefConfig;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

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
        try {
            String encryptedPassword = RsaEncryptor.encryptRSA(password, config.getPublicKey());
            String payload = buildConnectedIdPayload(bankId, encryptedPassword, orgCode);

//            log.info("Connected ID 생성 요청 - 은행코드: {}, 사용자ID: {}", orgCode, bankId);

            JsonNode response = codefApiClient.postJson(CodefApiConstants.CONNECTED_ID_URL, accessToken, payload);
            String connectedId = response.path("data").path("connectedId").asText(null);

            if (connectedId == null || connectedId.trim().isEmpty()) {
//                log.error("Connected ID 생성 실패 - 응답: {}", response.toPrettyString());
                throw new CustomException(ErrorCode.CODEF_LOGIN);
            }

//            log.info("✅ Connected ID 생성 성공");
            return connectedId;

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
//            log.error("Connected ID 생성 중 예외 발생", e);
            throw new CustomException(ErrorCode.CODEF_LOGIN);
        }
    }

    private String buildConnectedIdPayload(String bankId, String encryptedPassword, String orgCode) {
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
                              "password": "%s"
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