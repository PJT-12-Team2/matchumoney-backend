package team2.pjt12.matchumoney.domain.saving.codef;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;
import team2.pjt12.matchumoney.global.config.CodefConfig;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Component
public class CodefApiClient {

    private final CodefConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    //CODEF API Access Token 발급
    public String getAccessToken() {
        try {
            String credentials = config.getClientId() + ":" + config.getClientSecret();
            String basicAuth = Base64.encodeBase64String(credentials.getBytes(StandardCharsets.UTF_8));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://oauth.codef.io/oauth/token"))
                    .header("Authorization", "Basic " + basicAuth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&scope=read"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
//                log.error("❌ Access Token 발급 실패 - Status: {}, Body: {}",
//                        response.statusCode(), response.body());
                throw new CustomException(ErrorCode.CODEF_ERROR);
            }

            String accessToken = objectMapper.readTree(response.body()).get("access_token").asText();
//            log.info("✅ Access Token 발급 성공");
            return accessToken;

        } catch (HttpTimeoutException e) {
            log.error("CODEF API 타임아웃", e);
            throw new CustomException(ErrorCode.CODEF_TIMEOUT);
        } catch (JsonProcessingException e) {
            log.error("CODEF 응답 파싱 실패", e);
            throw new CustomException(ErrorCode.CODEF_RESPONSE_PARSE_ERROR);
        } catch (IOException e) {
            log.error("CODEF 통신 오류", e);
            throw new CustomException(ErrorCode.CODEF_COMMUNICATION_ERROR);
        } catch (Exception e) {
            log.error("CODEF 통신 오류", e);
            throw new CustomException(ErrorCode.CODEF_ERROR);
        }
    }

    //CODEF API POST 요청
    public JsonNode postJson(String url, String token, String payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                log.error("❌ API 호출 실패 - URL: {}, Status: {}, Body: {}",
                        url, response.statusCode(), response.body());
                throw new CustomException(ErrorCode.CODEF_ERROR);
            }

            String decodedBody = URLDecoder.decode(response.body(), StandardCharsets.UTF_8);
            return objectMapper.readTree(decodedBody);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("API 호출 중 예외 발생 - URL: {}", url, e);
            throw new CustomException(ErrorCode.CODEF_ERROR);
        }
    }
}
