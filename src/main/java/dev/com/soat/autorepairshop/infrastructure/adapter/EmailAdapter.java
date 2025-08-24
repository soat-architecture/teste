package dev.com.soat.autorepairshop.infrastructure.adapter;

import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailAdapter {
    private final JavaMailSender mailSender;

    public void send(final String from,
                     final String to,
                     final String subject,
                     final String message,
                     final Boolean isHtml){
        try {
            log.info("c=EmailAdapter m=send s=Start from={} to={} subject={}", from, to, subject);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, isHtml);

            mailSender.send(mimeMessage);
            log.info("c=EmailAdapter m=send s=Done from={} to={} subject={}", from, to, subject);
        }
        catch (Exception e){
            log.error("c=EmailAdapter m=send s=Error from={} to={} subject={} message={}", from, to, subject, e.getMessage());
            throw new GenericException("mail.sender.error");
        }
    }
}
