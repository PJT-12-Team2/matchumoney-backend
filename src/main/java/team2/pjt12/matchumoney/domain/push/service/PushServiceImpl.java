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
        // 중복 토큰 제거
        tokens = tokens.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (tokens.isEmpty()) {
            log.info("[PUSH] sendToTokens: NO TOKENS. title='{}' link='{}'", title, link);
            return 0;
        }

        WebpushConfig webpush = WebpushConfig.builder()
                .putHeader("TTL", "500")
                .putHeader("Urgency", "high")
                .setFcmOptions(WebpushFcmOptions.withLink(link))
                .build();

        Map<String,String> safeData = new java.util.HashMap<>((data == null) ? Map.of() : data);
        String traceId = UUID.randomUUID().toString();     // ← 배치/호출 단위 추적ID
        safeData.put("traceId", traceId);
        safeData.put("title", title);
        safeData.put("body", body);
        safeData.put("icon", "AlarmLogo.png"); // public 폴더 기준 경로
        safeData.put("link", link);

        log.info("[PUSH] build messages traceId={} tokens={}", traceId, tokens.size());
        log.info(" 로그 잘 찍히는 지 확인");

        final int BATCH = 50;
        int success = 0;

        for (int i = 0; i < tokens.size(); i += BATCH) {
            List<String> slice = tokens.subList(i, Math.min(i + BATCH, tokens.size()));
            List<Message> messages = new ArrayList<>(slice.size());
            for (String t : slice) {
                messages.add(Message.builder()
                        .setToken(t)
//                        .setNotification(notification)
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
                // 네트워크/인증 문제 등: 로그만 남기고 다음 배치 진행 (재시도는 스케줄러/아웃박스에서)
                e.printStackTrace();
            }
        }
        return success;
    }
}