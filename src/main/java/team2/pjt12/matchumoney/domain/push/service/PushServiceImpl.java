package team2.pjt12.matchumoney.domain.push.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.push.mapper.PushTokenMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushServiceImpl implements PushService {
    private final PushTokenMapper mapper;

    @Override
    public void upsert(long userId, String token, String userAgent) {
        mapper.upsert(userId, token, userAgent);
    }

    @Override
    public void delete(String token) {
        mapper.deleteByToken(token);
    }

    @Override
    public String sendTest(String token, String title, String body, String link) throws FirebaseMessagingException {
        WebpushConfig webpush = WebpushConfig.builder()
                .setFcmOptions(WebpushFcmOptions.withLink(link))
                .build();

        Message msg = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                .setWebpushConfig(webpush)
                .build();

        try {
            return FirebaseMessaging.getInstance().send(msg);
        } catch (FirebaseMessagingException e) {
            if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                mapper.deleteByToken(token);
            }
            throw new IllegalStateException("FCM send failed", e); // unchecked로 래핑
        }
    }

    @Override
    public int sendToUser(Long userId, String title, String body, String link, Map<String, String> data) {
        List<String> tokens = mapper.findTokensByUserId(userId);
        return sendToTokens(tokens, title, body, link, data);
    }

    @Override
    public int sendToUsers(List<Long> userIds, String title, String body, String link, Map<String, String> data) {
        if (userIds == null || userIds.isEmpty()) {
            log.info("[PUSH] sendToUsers called with empty userIds");
            return 0;
        }
        List<String> tokens = mapper.findTokensByUserIds(userIds);
        log.info("[PUSH] tokens fetched size={}", tokens == null ? 0 : tokens.size());
        return sendToTokens(tokens, title, body, link, data);
    }

    private int sendToTokens(List<String> tokens, String title, String body, String link, Map<String, String> data) {
        if (tokens == null) {
            tokens = List.of();
        }
        tokens = tokens.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (tokens.isEmpty()) {
            log.info("[PUSH] sendToTokens: NO TOKENS. title='{}' link='{}'", title, link);
            return 0;
        }

        // ① 링크 정규화: 호스트 차이/트레일링 슬래시 차이 제거
        String normalizedLink = normalizeLink(link);

        WebpushConfig webpush = WebpushConfig.builder()
                .putHeader("TTL", "500")
                .putHeader("Urgency", "high")
                // withLink에는 정규화 전/후 어느 쪽을 넣어도 되지만, 프론트 캐논키와 일치성을 위해 정규화 버전 사용 권장
                .setFcmOptions(WebpushFcmOptions.withLink(normalizedLink))
                .build();

        Map<String, String> safeData = new HashMap<>((data == null) ? Map.of() : data);

        // ② 추적/표시용 공통 데이터
        String traceId = UUID.randomUUID().toString();
        safeData.put("traceId", traceId);
        safeData.put("title", title);
        safeData.put("body", body);
        safeData.put("icon", "AlarmLogo.png");     // public 기준
        safeData.put("link", normalizedLink);

        // ③ 프론트/서버 모두가 동일하게 계산할 수 있는 캐노니컬 키
        String type = Optional.ofNullable(safeData.get("type")).orElse("unknown");
        String canonicalKey = type + "|" + title + "|" + body + "|" + normalizedLink;
        safeData.put("canonicalKey", canonicalKey);

        log.info("[PUSH] build messages traceId={} tokens={} canonicalKey={}", traceId, tokens.size(), canonicalKey);

        final int BATCH = 50;
        int success = 0;

        for (int i = 0; i < tokens.size(); i += BATCH) {
            List<String> slice = tokens.subList(i, Math.min(i + BATCH, tokens.size()));
            List<Message> messages = new ArrayList<>(slice.size());
            for (String t : slice) {
                messages.add(Message.builder()
                        .setToken(t)
                        // .setNotification(...)  <-- 시스템 알림 중복을 막기 위해 계속 비활성화(데이터 전송만)
                        .putAllData(safeData)
                        .setWebpushConfig(webpush)
                        .build());
            }

            try {
                BatchResponse resp = FirebaseMessaging.getInstance().sendEach(messages);
                success += resp.getSuccessCount();

                // 실패 토큰 정리
                List<SendResponse> rs = resp.getResponses();
                for (int k = 0; k < rs.size(); k++) {
                    SendResponse r = rs.get(k);
                    if (!r.isSuccessful()) {
                        Exception ex = r.getException();
                        if (ex instanceof FirebaseMessagingException fme &&
                                fme.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                            String badToken = slice.get(k);
                            mapper.deleteByToken(badToken);
                        }
                    }
                }
            } catch (FirebaseMessagingException e) {
                // 네트워크/인증 문제 등: 로그만 남기고 다음 배치 진행
                log.warn("[PUSH] sendEach failed traceId={} cause={}", traceId, e.toString(), e);
            }
        }
        return success;
    }

    /** 호스트/프로토콜에 상관없이 프론트에서 동일하게 인식되도록 link를 정규화 */
    private String normalizeLink(String link) {
        if (link == null || link.isBlank()) return "/";
        // 로컬 개발 호스트 제거
        String s = link
                .replace("http://localhost:5173", "")
                .replace("https://localhost:5173", "")
                .replace("http://127.0.0.1:5173", "")
                .replace("https://127.0.0.1:5173", "");
        // 도메인을 포함한 배포 주소가 있다면 동일하게 제거 규칙을 추가해 주세요.
        // 예: .replace("https://app.example.com", "")

        if (s.isBlank()) s = "/";
        // 쿼리/해시는 유지, 트레일링 슬래시 통일
        if (!s.startsWith("/")) s = "/" + s;
        // "/" 하나만 남기거나, 다른 경로는 끝의 슬래시 제거
        if (s.length() > 1 && s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return s;
    }
}