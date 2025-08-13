package team2.pjt12.matchumoney.domain.push.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.push.mapper.PushTokenMapper;

@Service
@RequiredArgsConstructor
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
}