package team2.pjt12.matchumoney.domain.mydata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardTransactionVO;
import team2.pjt12.matchumoney.global.config.MyDataConfig;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class KBCardApiUtil {

    private static final String ORG_CODE = "0301"; // 국민카드 조직코드
    private static final String INQUIRY_TYPE_CARD_LIST = "1"; // 카드 목록 조회
    private static final String INQUIRY_TYPE_APPROVAL_LIST = "0"; // 승인 내역 조회 (0: 전체, 1: 승인만, 2: 취소만)

    // HttpClient 인스턴스 재사용
    private final HttpClient httpClient;
    private final MyDataConfig myDataConfig;

    public KBCardApiUtil(MyDataConfig myDataConfig) {
        this.myDataConfig = myDataConfig;
        this.httpClient = HttpClient.newHttpClient();
    }

    public List<CardHoldingVO> fetchKbCards(String kbId, String kbPw, Long userId) throws Exception {
        String token = getAccessToken();
        if (token == null) throw new RuntimeException("CODEF 인증 실패");

        String encryptedPw = encryptRSA(kbPw);
        String connectedId = createConnectedId(token, encryptedPw, ORG_CODE, kbId);
        if (connectedId == null) throw new RuntimeException("connectedId 발급 실패");

        return getCardList(token, connectedId, userId);
    }

    // 카드 거래 내역 조회 메서드 추가
    public List<CardTransactionVO> fetchKbCardTransactions(
            String connectedId, String cardNo, String cardPw2, String birthDate, LocalDate startDate, LocalDate endDate,
            Long userId, Long cardInfoFinId, Integer cardInfoCardId, String cardNameFromHolding) throws Exception { // cardInfoFinId, cardInfoCardId 추가
        String token = getAccessToken();
        if (token == null) throw new RuntimeException("CODEF 인증 실패");

        // CodeF API 요청에 필요한 값 준비 (카드번호는 평문, 비밀번호만 암호화)
//        System.out.println("원본 카드번호 길이: " + cardNo.length() + ", 값: " + cardNo);
//        System.out.println("원본 카드비밀번호: " + cardPw2);
        String encryptedCardPw2 = encryptRSA(cardPw2);
//        System.out.println("생년월일: " + birthDate);

        return getApprovalList(token, connectedId, birthDate, startDate, endDate,
                cardNo, encryptedCardPw2, userId, cardInfoFinId, cardInfoCardId, cardNameFromHolding);
    }

    private String getAccessToken() throws Exception {
        String basicAuth = java.util.Base64.getEncoder().encodeToString(
                (myDataConfig.getClientId() + ":" + myDataConfig.getClientSecret()).getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth.codef.io/oauth/token"))
                .header("Authorization", "Basic " + basicAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&scope=read"))
                .build();

        HttpResponse<String> res = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) {
            return new ObjectMapper().readTree(res.body()).get("access_token").asText();
        } else {
            System.err.println("❌ Access Token 발급 실패: " + res.body());
            return null;
        }
    }

    private String encryptRSA(String text) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        byte[] decoded = java.util.Base64.getDecoder().decode(myDataConfig.getPublicKey());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(spec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        // UTF-8로 인코딩하여 암호화
        return java.util.Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes(StandardCharsets.UTF_8)));
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

        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        String decoded = URLDecoder.decode(res.body(), StandardCharsets.UTF_8);
        try {
            JsonNode node = new ObjectMapper().readTree(decoded);
            JsonNode data = node.get("data");
            if (data == null || data.isMissingNode() || data.isNull()) {
                System.err.println("❌ CODEF 응답에 'data' 노드가 없습니다: " + decoded);
                throw new RuntimeException("CODEF 응답에 'data' 노드 누락");
            }
            JsonNode connectedIdNode = data.get("connectedId");
            if (connectedIdNode == null || connectedIdNode.isMissingNode() || connectedIdNode.isNull()) {
                System.err.println("❌ CODEF 응답에 'connectedId'가 없습니다: " + decoded);
                throw new RuntimeException("CODEF 응답에 'connectedId' 누락");
            }
            return connectedIdNode.asText();
        } catch (Exception e) {
            System.err.println("❌ JSON 파싱 실패 또는 응답 구조 오류: " + decoded);
            throw new RuntimeException("CODEF connectedId 발급 중 오류 발생", e);
        }
    }

    private List<CardHoldingVO> getCardList(String token, String connectedId, Long userId) throws Exception {
        List<CardHoldingVO> cardHoldings = new ArrayList<>();
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
                """, ORG_CODE, connectedId, INQUIRY_TYPE_CARD_LIST);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://development.codef.io/v1/kr/card/p/account/card-list"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson))
                .build();

        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        String decoded = URLDecoder.decode(res.body(), StandardCharsets.UTF_8);

        JsonNode rootNode = new ObjectMapper().readTree(decoded);
        JsonNode dataNode = rootNode.get("data");

        if (dataNode == null || dataNode.isNull() || dataNode.size() == 0) {
            return cardHoldings;
        }

        if (dataNode.isArray()) {
            for (JsonNode card : dataNode) {
                cardHoldings.add(parseCardNode(card, userId, connectedId));
            }
        } else if (dataNode.has("resCardNo")) { // 단일 카드 정보가 오는 경우
            cardHoldings.add(parseCardNode(dataNode, userId, connectedId));
        }
        return cardHoldings;
    }

    private CardHoldingVO parseCardNode(JsonNode card, Long userId, String connectedId) {
        CardHoldingVO vo = new CardHoldingVO();

        // CodeF 응답에서 "card_id"와 관련된 정보는 없음. 초기값을 null로 설정하고 매칭 로직에서 처리
        // null: 아직 매칭되지 않은 상태 (매칭 로직에서 카드고릴라 ID로 업데이트 예정)
        vo.setCardId(null); // 초기값: 매칭 전 상태

        String resCardNo = card.hasNonNull("resCardNo") && !card.path("resCardNo").asText().isEmpty()
                ? card.path("resCardNo").asText()
                : "DEFAULT_CARD_NO_" + UUID.randomUUID().toString().substring(0, 8); // 간략하게 UUID 일부 사용
        vo.setResCardNo(resCardNo);

        // finId는 unique constraint에 걸리므로, connectedId와 resCardNo를 조합
        // 단순히 hashCode는 충돌 가능성이 있으므로 더 robust한 방법 고려 필요
        long finIdComposite = (long) (connectedId.hashCode() * 31 + resCardNo.hashCode());
        if (finIdComposite < 0) finIdComposite *= -1; // 양수로 변환
        vo.setFinId(finIdComposite);

        vo.setDiscontinued(0); // 기본값 0 (false)
        vo.setCardName(card.hasNonNull("resCardName") && !card.path("resCardName").asText().isEmpty()
                ? card.path("resCardName").asText() : "미등록 카드");
        vo.setResSleepYn(card.hasNonNull("resSleepYn") && !card.path("resSleepYn").asText().isEmpty()
                ? card.path("resSleepYn").asText() : "N");
        vo.setResCardType(card.hasNonNull("resCardType") && !card.path("resCardType").asText().isEmpty()
                ? card.path("resCardType").asText() : "신용");
        vo.setResTrafficYn(card.hasNonNull("resTrafficYn") && !card.path("resTrafficYn").asText().isEmpty()
                ? card.path("resTrafficYn").asText() : "N");
        vo.setResImageLink(card.hasNonNull("resImageLink") && !card.path("resImageLink").asText().isEmpty()
                ? card.path("resImageLink").asText() : null);
        vo.setResIssueDate(card.hasNonNull("resIssueDate") && !card.path("resIssueDate").asText().isEmpty()
                ? card.path("resIssueDate").asText() : "20250101");
        vo.setResValidPeriod(card.hasNonNull("resValidPeriod") && !card.path("resValidPeriod").asText().isEmpty()
                ? card.path("resValidPeriod").asText() : "202601");
        vo.setResState(card.hasNonNull("resState") && !card.path("resState").asText().isEmpty()
                ? card.path("resState").asText() : "정상");
        vo.setUserId(userId != null ? userId : 1L);
        vo.setConnectedId(connectedId);
        return vo;
    }

    // 카드 승인내역(거래내역) 조회 및 파싱 메서드
    private List<CardTransactionVO> getApprovalList(
            String token, String connectedId, String birthDate, LocalDate startDate, LocalDate endDate,
            String cardNo, String encryptedCardPw2, Long userId, Long cardInfoFinId, Integer cardInfoCardId, String cardNameFromHolding) throws Exception {

        List<CardTransactionVO> transactions = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String inputJson = String.format(
                """
                {
                    "organization": "%s",
                    "connectedId": "%s",
                    "birthDate": "%s",
                    "startDate": "%s",
                    "endDate": "%s",
                    "orderBy": "0",
                    "inquiryType": "%s",
                    "cardName": "",
                    "duplicateCardIdx": "",
                    "cardNo": "%s",
                    "cardPassword": "%s",
                    "memberStoreInfoType": ""
                }
                """, ORG_CODE, connectedId, birthDate,
                startDate.format(formatter), endDate.format(formatter),
                INQUIRY_TYPE_APPROVAL_LIST, cardNo, encryptedCardPw2);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://development.codef.io/v1/kr/card/p/account/approval-list"))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(inputJson))
                .build();

        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        String decoded = URLDecoder.decode(res.body(), StandardCharsets.UTF_8);

        System.out.println("\n📥 CODEF 카드 승인내역 API 응답:\n" + decoded);

        JsonNode rootNode = new ObjectMapper().readTree(decoded);
        JsonNode dataNode = rootNode.get("data");

        if (dataNode == null || dataNode.isNull()) {
            System.out.println("❌ 승인내역 없음 또는 API 오류 (data 노드 누락)!");
            return transactions;
        }

        // data가 배열인 경우 (일반적인 경우)
        if (dataNode.isArray()) {
            System.out.println("✅ 승인내역 " + dataNode.size() + "건 발견!");
            for (JsonNode approval : dataNode) {
                transactions.add(parseTransactionNode(approval, userId, cardInfoFinId, cardInfoCardId, cardNameFromHolding, startDate, endDate));
            }
        } 
        // data.list가 배열인 경우
        else if (dataNode.isObject() && dataNode.has("list") && dataNode.get("list").isArray()) {
            JsonNode listNode = dataNode.get("list");
            System.out.println("✅ 승인내역 " + listNode.size() + "건 발견!");
            for (JsonNode approval : listNode) {
                transactions.add(parseTransactionNode(approval, userId, cardInfoFinId, cardInfoCardId, cardNameFromHolding, startDate, endDate));
            }
        }
        // data가 단일 객체인 경우
        else if (dataNode.isObject() && dataNode.has("resUsedAmount")) {
            System.out.println("✅ 승인내역 1건 발견!");
            transactions.add(parseTransactionNode(dataNode, userId, cardInfoFinId, cardInfoCardId, cardNameFromHolding, startDate, endDate));
        } 
        else {
            System.out.println("❌ 승인내역 없음 또는 예상치 못한 응답 형식!");
            System.out.println("data 구조: " + dataNode.toString());
        }
        return transactions;
    }

    private CardTransactionVO parseTransactionNode(
            JsonNode approval, Long userId, Long cardInfoFinId, Integer cardInfoCardId, String cardNameFromHolding, // 매개변수 수정
            LocalDate commStartDate, LocalDate commEndDate) {
        CardTransactionVO vo = new CardTransactionVO();

        vo.setFinId(cardInfoFinId); // CardHoldingVO의 finId 값으로 설정
        // 매칭 실패시 null 허용 (외래키 제약조건 해제 후)
        vo.setCardId2(cardInfoCardId); // CardHoldingVO의 cardId (카드고릴라 idx) 값으로 설정, null 가능
        vo.setUserId(userId);
        vo.setCardName(cardNameFromHolding);

        // 필수 필드에 대한 null/empty 체크 및 기본값 설정
        vo.setResUsedDate(approval.path("resUsedDate").asText(""));
        vo.setResUsedTime(approval.path("resUsedTime").asText(""));
        vo.setResCardNo(approval.path("resCardNo").asText(""));
        vo.setResCardNo1(approval.path("resCardNo1").asText(null));
        vo.setResCardName(approval.path("resCardName").asText(""));
        vo.setResMemberStoreName(approval.path("resMemberStoreName").asText(""));
        vo.setResUsedAmount(approval.path("resUsedAmount").asLong(0L));
        vo.setResPaymentType(approval.path("resPaymentType").asText(""));
        vo.setResInstallmentMonth(approval.path("resInstallmentMonth").asText(null));
        vo.setResApprovalNo(approval.path("resApprovalNo").asText(""));
        vo.setResPaymentDueDate(approval.path("resPaymentDueDate").asText(null));
        vo.setResHomeForeignType(approval.path("resHomeForeignType").asText(""));
        vo.setResMemberStoreCorpNo(approval.path("resMemberStoreCorpNo").asText(null));
        vo.setResMemberStoreType(approval.path("resMemberStoreType").asText(null));
        vo.setResMemberStoreTelNo(approval.path("resMemberStoreTelNo").asText(null));
        vo.setResMemberStoreAddr(approval.path("resMemberStoreAddr").asText(null));
        vo.setResMemberStoreNo(approval.path("resMemberStoreNo").asText(null));
        vo.setResCancelYn(approval.path("resCancelYN").asText("N"));
        vo.setResCancelAmount(approval.path("resCancelAmount").asLong(0L));
        vo.setResVat(approval.path("resVat").asLong(0L));
        vo.setResCashBack(approval.path("resCashBack").asLong(0L));
        vo.setResKrwAmt(approval.path("resKrwAmt").asLong(0L));
        vo.setResAccountCurrency(approval.path("resAccountCurrency").asText("KRW"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        vo.setCommStartDate(commStartDate.format(formatter));
        vo.setCommEndDate(commEndDate.format(formatter));

        return vo;
    }
}