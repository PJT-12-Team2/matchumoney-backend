package team2.pjt12.matchumoney.domain.mydata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;
import team2.pjt12.matchumoney.domain.mydata.vo.CardInfoVO;

import javax.crypto.Cipher;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;   // UUID 클래스 추가

@Component
public class KBCardApiUtil {

    private static final String CLIENT_ID     = "";
    private static final String CLIENT_SECRET = "";
    private static final String PUBLIC_KEY = "";
    private static final String ORG_CODE = "0301";
    private static final String INQUIRY_TYPE = "1";

    public List<CardInfoVO> fetchKbCards(String kbId, String kbPw, Long userId) throws Exception {
        String token = getAccessToken();
        if (token == null) throw new RuntimeException("CODEF 인증 실패");

        String encryptedPw = encryptRSA(kbPw);
        String connectedId = createConnectedId(token, encryptedPw, ORG_CODE, kbId);
        if (connectedId == null) throw new RuntimeException("connectedId 발급 실패");

        return getCardList(token, connectedId, userId);
    }

    private String getAccessToken() throws Exception {
        String basicAuth = Base64.encodeBase64String((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth.codef.io/oauth/token"))
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&scope=read"))
                .build();

        HttpResponse<String> res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) {
            return new ObjectMapper().readTree(res.body()).get("access_token").asText();
        } else {
            System.err.println("❌ Access Token 발급 실패: " + res.body());
            return null;
        }
    }

    private String encryptRSA(String text) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        byte[] decoded = Base64.decodeBase64(PUBLIC_KEY);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(spec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.encodeBase64String(cipher.doFinal(text.getBytes()));
    }

    private String createConnectedId(String token, String encryptedPw, String orgCode, String cardId) throws Exception {
        String payload = String.format(
                """
                {
                  "accountList":[
                    {
                      "countryCode":"KR",
                      "businessType":"CD",
                      "clientType":"P",
                      "organization":"%s",
                      "loginType":"1",
                      "id":"%s",
                      "password":"%s"
                    }
                  ]
                }
                """, orgCode, cardId, encryptedPw);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://development.codef.io/v1/account/create"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> res = HttpClient.newHttpClient()
                .send(req, HttpResponse.BodyHandlers.ofString());

        String decoded = java.net.URLDecoder.decode(res.body(), StandardCharsets.UTF_8);
//        System.err.println("CODEF 응답: " + decoded);
        try {
            JsonNode node = new ObjectMapper().readTree(decoded);
            String cid = node.get("data").get("connectedId").asText();
            return cid;
        } catch (Exception e) {
            System.err.println("❌ JSON 파싱 실패: " + decoded);
            return null;
        }
    }

    private List<CardInfoVO> getCardList(String token, String connectedId, Long userId) throws Exception {
        List<CardInfoVO> cardHoldings = new ArrayList<>();
        String inputJson = String.format(
                """
                {
                    "organization": "%s",
                    "connectedId": "%s",
                    "cardNo": "",
                    "cardPassword": "",
                    "birthDate": "",
                    "inquiryType": "%s"
                }
                """, ORG_CODE, connectedId, INQUIRY_TYPE);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://development.codef.io/v1/kr/card/p/account/card-list"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson))
                .build();

        HttpResponse<String> res = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        String decoded = URLDecoder.decode(res.body(), StandardCharsets.UTF_8);

        JsonNode rootNode = new ObjectMapper().readTree(decoded);
        JsonNode dataNode = rootNode.get("data");

        if (dataNode == null || dataNode.isNull() || dataNode.size() == 0) {
            return cardHoldings;
        }

        if (dataNode.isArray()) {
            for (JsonNode card : dataNode) {
                cardHoldings.add(parseCardNode(card, userId));
            }
        } else if (dataNode.has("resCardNo")) {
            cardHoldings.add(parseCardNode(dataNode, userId));
        }
        return cardHoldings;
    }

    private CardInfoVO parseCardNode(JsonNode card, Long userId) {
        CardInfoVO vo = new CardInfoVO();

        vo.setCardId(1);

        String resCardNo = card.hasNonNull("resCardNo") ? card.path("resCardNo").asText() : "DEFAULT_CARD_NO_" + UUID.randomUUID().toString();
        vo.setResCardNo(resCardNo);

        long finIdComposite = (long) (resCardNo.hashCode() * 31 + (userId != null ? userId.hashCode() : 0));
        vo.setFinId(finIdComposite);

        vo.setDiscontinued(0); // NOT NULL이므로 0 (false)으로 설정
        vo.setCardName(card.hasNonNull("resCardName") ? card.path("resCardName").asText() : "테스트카드");
        vo.setResSleepYn(card.hasNonNull("resSleepYn") ? card.path("resSleepYn").asText() : "N");
        vo.setResCardType(card.hasNonNull("resCardType") ? card.path("resCardType").asText() : "신용");
        vo.setResTrafficYn(card.hasNonNull("resTrafficYn") ? card.path("resTrafficYn").asText() : "N");
        vo.setResImageLink(card.hasNonNull("resImageLink") ? card.path("resImageLink").asText() : null);
        vo.setResIssueDate(card.hasNonNull("resIssueDate") ? card.path("resIssueDate").asText() : "20250101");
        vo.setResValidPeriod(card.hasNonNull("resValidPeriod") ? card.path("resValidPeriod").asText() : "202601");
        vo.setResState(card.hasNonNull("resState") ? card.path("resState").asText() : "정상");
        vo.setUserId(userId != null ? userId : 1L); // userId가 null이 아닐 경우 사용, 아니면 기본값 1L
        return vo;
    }
}