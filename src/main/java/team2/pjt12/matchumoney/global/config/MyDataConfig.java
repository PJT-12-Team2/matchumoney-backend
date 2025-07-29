package team2.pjt12.matchumoney.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class MyDataConfig {
    
    @Value("${mydata.clientId}")
    private String clientId;
    
    @Value("${mydata.clientSecret}")
    private String clientSecret;
    
    @Value("${mydata.publicKey}")
    private String publicKey;
    
    public String getClientId() {
        return clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public String getPublicKey() {
        return publicKey;
    }
}