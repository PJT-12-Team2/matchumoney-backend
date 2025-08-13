package team2.pjt12.matchumoney.domain.push.service;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface PushService {
    void upsert(long userId, String token, String userAgent);

    void delete(String token);

    String sendTest(String token, String title, String body, String link)
            throws FirebaseMessagingException;
}
