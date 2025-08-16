package team2.pjt12.matchumoney.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// CodefConfig.java
@Configuration
@Getter
@PropertySource("classpath:application.properties")
public class CodefConfig {

    @Value("${mydata.clientId}")
    private String clientId;

    @Value("${mydata.clientSecret}")
    private String clientSecret;

    @Value("${mydata.publicKey}")
    private String publicKey;
}
