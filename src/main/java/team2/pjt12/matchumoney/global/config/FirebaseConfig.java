package team2.pjt12.matchumoney.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) return;

        GoogleCredentials creds = resolveCredentials();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(creds)
                .build();

        FirebaseApp.initializeApp(options);
    }

    private GoogleCredentials resolveCredentials() throws Exception {
        String keyPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        if (keyPath != null && !keyPath.isBlank()) {
            try (InputStream in = new FileInputStream(keyPath)) {
                return GoogleCredentials.fromStream(in);
            }
        }

        // 2) JSON 문자열 방식 (FIREBASE_CREDENTIALS_JSON에 전체 JSON)
        String json = System.getenv("FIREBASE_CREDENTIALS_JSON");
        if (json != null && !json.isBlank()) {
            try (InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                return GoogleCredentials.fromStream(in);
            }
        }

        // 3) ADC (EC2 IAM Role 등)
        return GoogleCredentials.getApplicationDefault();
    }
}