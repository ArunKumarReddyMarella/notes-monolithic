package com.enotes.monolithic.service;

import com.enotes.monolithic.dto.EmailRequest;
import com.enotes.monolithic.entity.User;

public interface EmailService {
    public void sendRegistrationEmail(User user, EmailRequest emailRequest, String baseUrl);

    public void sendResetPasswordEmail(User user, String baseUrl);
}
