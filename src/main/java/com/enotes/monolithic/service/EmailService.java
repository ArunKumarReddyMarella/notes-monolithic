package com.enotes.monolithic.service;

import com.enotes.monolithic.dto.EmailRequest;
import com.enotes.monolithic.entity.User;

public interface EmailService {
    public void sendEmail(User user, EmailRequest emailRequest, String url);
}
