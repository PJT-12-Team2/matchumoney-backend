package team2.pjt12.matchumoney.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * AWS S3 Presigner Bean 설정
 * 환경변수:
 *  - AWS_ACCESS_KEY_ID
 *  - AWS_SECRET_ACCESS_KEY
 *  - AWS_REGION (예: ap-northeast-2)
 */
@Configuration
public class S3Config {

    @Bean
    public S3Presigner s3Presigner() {
        String regionStr = System.getenv("AWS_REGION"); // 또는 System.getProperty("aws.region")
        System.out.println("[S3Config] AWS_REGION=" + regionStr);
        return S3Presigner.builder()
                .region(software.amazon.awssdk.regions.Region.of(
                        regionStr != null && !regionStr.isBlank() ? regionStr : "ap-northeast-2"
                ))
                .credentialsProvider(software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create())
                .build();
    }

}
