package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.EmailRequest;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.exception.EmailException;
import com.enotes.monolithic.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendRegistrationEmail(User user, EmailRequest emailRequest, String baseUrl) {
        try {
            Context context = new Context();
            context.setVariable("title", emailRequest.getTitle());
            context.setVariable("message", emailRequest.getMessage());
            context.setVariable("verificationLink", baseUrl + "/api/v1/auth/user/verify-account?userId=" + user.getId() + "&verificationCode=" + user.getAccountStatus().getVerificationCode()); // Replace with your verification link logicurl + "/api/v1/auth/user/verify-account?userId=" + user.getId() + "&verificationCode=" + user.getAccountStatus().getVerificationCode());

            // Process the template
            String htmlContent = templateEngine.process("account-verification-email", context);

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
            throw new EmailException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendResetPasswordEmail(User user, String baseUrl) {
        try {
            Context context = new Context();
            context.setVariable("uid", user.getId());
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("resetLink", baseUrl + "/api/v1/auth/user/verify-reset-password-code?userId=" + user.getId() + "&code=" + user.getAccountStatus().getResetPasswordCode());

            // Process the template
            String htmlContent = templateEngine.process("email-reset-password", context);

            // Create and send email
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Reset Password");
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            throw new EmailException("Failed to send email: " + e.getMessage());
        }
    }
}
