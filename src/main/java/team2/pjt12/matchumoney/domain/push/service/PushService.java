package team2.pjt12.matchumoney.domain.push.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;
import java.util.Map;

public interface PushService {
    void upsert(long userId, String token, String userAgent);

    void delete(String token);

    String sendTest(String token, String title, String body, String link)
            throws FirebaseMessagingException;

    int sendToUser(Long userId, String title, String body, String link, Map<String,String> data);

    int sendToUsers(List<Long> userIds, String title, String body, String link, Map<String,String> data);
}
