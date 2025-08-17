package team2.pjt12.matchumoney.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
    @PostConstruct
    public void init() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) return;

        String json = System.getenv("FIREBASE_CREDENTIALS_JSON"); // 서비스계정 JSON 문자열
        GoogleCredentials creds = (json != null)
                ? GoogleCredentials.fromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                : GoogleCredentials.getApplicationDefault();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(creds)
                .build();

        FirebaseApp.initializeApp(options);
    }
}