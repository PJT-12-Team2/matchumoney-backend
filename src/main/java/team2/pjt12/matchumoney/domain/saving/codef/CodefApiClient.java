package team2.pjt12.matchumoney.domain.saving.codef;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import team2.pjt12.matchumoney.global.config.CodefConfig;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CodefApiClient {

    private final CodefConfig config;
    private final EasyCodef codef;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Spring Boot 기본 빈 사용

    /**
     * v1 요청: requestProduct(String productUrl, EasyCodefServiceType serviceType, HashMap<String,Object> params)
     * 반환: String(JSON) → JsonNode로 변환
     */
    public JsonNode postJson(String productUrl, HashMap<String, Object> params) {
        try {
            EasyCodefServiceType serviceType = config.getServiceType();

            String resultJson = codef.requestProduct(productUrl, serviceType, params);
            JsonNode root = objectMapper.readTree(resultJson);

            String code = root.path("result").path("code").asText();
            if (!"CF-00000".equals(code)) {
                String msg = root.path("result").path("message").asText();
                log.error("CODEF API 실패 - url={}, code={}, message={}", productUrl, code, msg);
                throw new CustomException(ErrorCode.CODEF_ERROR);
            }
            return root;
        } catch (CustomException e) {
            throw e;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new CustomException(ErrorCode.CODEF_RESPONSE_PARSE_ERROR);
        } catch (Exception e) {
            log.error("CODEF 호출 예외 - url={}", productUrl, e);
            throw new CustomException(ErrorCode.CODEF_ERROR);
        }
    }
}
