package com.enotes.monolithic.service;

import com.enotes.monolithic.dto.EmailRequest;
import com.enotes.monolithic.entity.User;
import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Boolean> sendRegistrationEmail(User user, EmailRequest emailRequest, String baseUrl);
    CompletableFuture<Boolean> sendResetPasswordEmail(User user, String baseUrl);
}