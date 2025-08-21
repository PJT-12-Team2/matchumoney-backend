package team2.pjt12.matchumoney.global.config;

import io.codef.api.EasyCodef;
import io.codef.api.EasyCodefServiceType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// CodefConfig.java
@Configuration
@Getter
@PropertySource("classpath:application.properties")
public class CodefConfig {

    // application.properties 의 값들을 DEMO 용으로 사용
    @Value("${mydata.clientId}")
    private String clientId;

    @Value("${mydata.clientSecret}")
    private String clientSecret;

    @Value("${mydata.publicKey}")
    private String publicKey;

    /**
     * 데모(SANDBOX) 고정 — 운영 전환 시 PRODUCTION 으로 교체
     */
    public EasyCodefServiceType getServiceType() {
        return EasyCodefServiceType.DEMO;
    }

    /**
     * easycodef-java 1.0.6 클라이언트 빈
     */
    @Bean
    public EasyCodef easyCodef() {
        EasyCodef codef = new EasyCodef();
        // 데모 자격 설정
        codef.setClientInfoForDemo(clientId, clientSecret);
        // 퍼블릭키 설정
        codef.setPublicKey(publicKey);
        return codef;
    }
}