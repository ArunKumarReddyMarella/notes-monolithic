package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.EmailRequest;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.exception.EmailException;
import com.enotes.monolithic.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async("emailTaskExecutor")
    @Retryable(
            value = {EmailException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public CompletableFuture<Boolean> sendRegistrationEmail(User user, EmailRequest emailRequest, String baseUrl) {
        try {
            Context context = new Context();
            context.setVariable("title", emailRequest.getTitle());
            context.setVariable("message", emailRequest.getMessage());
            context.setVariable("verificationLink", baseUrl +
                    "/api/v1/auth/user/verify-account?userId=" + user.getId() +
                    "&verificationCode=" + user.getAccountStatus().getVerificationCode());

            String htmlContent = templateEngine.process("account-verification-email", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(emailRequest.getTo());
            helper.setSubject(emailRequest.getSubject());
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", emailRequest.getTo());
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to send registration email to {}: {}", emailRequest.getTo(), e.getMessage());
            throw new EmailException("Failed to send email: " + e.getMessage());
        }
    }

    @Override
    @Async("emailTaskExecutor")
    @Retryable(
            value = {EmailException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2.0)
    )
    public CompletableFuture<Boolean> sendResetPasswordEmail(User user, String baseUrl) {
        try {
            Context context = new Context();
            context.setVariable("uid", user.getId());
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("resetLink", baseUrl +
                    "/api/v1/auth/user/verify-reset-password-code?userId=" + user.getId() +
                    "&code=" + user.getAccountStatus().getResetPasswordCode());

            String htmlContent = templateEngine.process("email-reset-password", context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Reset Password");
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Password reset email sent successfully to: {}", user.getEmail());
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
            throw new EmailException("Failed to send email: " + e.getMessage());
        }
    }
}