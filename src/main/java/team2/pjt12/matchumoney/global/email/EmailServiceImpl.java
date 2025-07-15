package team2.pjt12.matchumoney.global.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public boolean sendEmail(String email, String title, String content) {
        // 이메일 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true); // true = HTML 허용

            // 보내는 사람 이름 지정 가능
            helper.setFrom(new InternetAddress("matchumoney@gmail.com", "맞추머니"));

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // 로그로 예외 확인
            return false;
        }
    }
}