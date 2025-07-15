package team2.pjt12.matchumoney.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); // SMTP 서버
        mailSender.setPort(587);              // SMTP 포트
        mailSender.setUsername("${EMAIL_USERNAME}"); // 발신자 이메일 주소
        mailSender.setPassword("${EMAIL_PASSWORD}");

        mailSender.setDefaultEncoding("UTF-8");

        // 메일 전송 프로퍼티
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // 디버그 로그 출력 원하면 true

        return mailSender;
    }
}