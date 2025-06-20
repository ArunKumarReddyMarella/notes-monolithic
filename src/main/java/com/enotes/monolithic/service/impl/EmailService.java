package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.EmailRequest;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(User user, EmailRequest emailRequest, String url) {
        try {
            Context context = new Context();
            context.setVariable("title", emailRequest.getTitle());
            context.setVariable("message", emailRequest.getMessage());
            context.setVariable("verificationLink", url + "/api/v1/user/verify-account?userId=" + user.getId() + "&verificationCode=" + user.getAccountStatus().getVerificationCode());

            // Process the template
            String htmlContent = templateEngine.process("email", context);

            // Create and send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(fromEmail);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", emailRequest.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", emailRequest.getTo(), e);
            throw new EmailException("Failed to send email: " + e.getMessage());
        }
    }
}
