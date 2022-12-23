package com.minswap.hrms.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject,String body) throws MessagingException {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("no-reply@ms-hrms.software");
//        message.setTo(toEmail);
//        message.setText(body);
//        message.setSubject(subject);
//
//        mailSender.send(message);
        MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            helper.setFrom("no-reply@ms-hrms.software");
            helper.setTo(toEmail);
            helper.setText(body,true);
            helper.setSubject(subject);

            mailSender.send(message);
    }
}
