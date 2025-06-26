package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.PasswordChngRequest;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.exception.ResourceNotFoundException;
import com.enotes.monolithic.repository.UserRepository;
import com.enotes.monolithic.service.EmailService;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void resetPassword(PasswordChngRequest passwordChngRequest) {
        User user = CommonUtil.getLoggedInUser();
        String currentPassword = passwordChngRequest.getCurrentPassword();
        String newPassword = passwordChngRequest.getNewPassword();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public void resetPassword(Integer userId, String newPassword) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getAccountStatus().getResetPasswordCode() == null) {
            throw new IllegalArgumentException("Reset password code not found or already used");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.getAccountStatus().setResetPasswordCode(null);
        userRepo.save(user);
    }

    @Override
    public void sendEmailForResetPassword(String email, String baseUrl) throws Exception {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate unique reset password token
        String resetPasswordCode = UUID.randomUUID().toString();
        user.getAccountStatus().setResetPasswordCode(resetPasswordCode);
        User updatedUser = userRepo.save(user);

        emailService.sendResetPasswordEmail(updatedUser,baseUrl);

    }

    @Override
    public void verifyResetPasswordCode(Integer userId, String code) throws Exception {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        verificationCodeValidation(user.getAccountStatus().getResetPasswordCode(), code);
    }

    private void verificationCodeValidation(String existingCode, String code) {
        if(!StringUtils.hasText(existingCode)) {
            throw new IllegalArgumentException("Reset password code not found or already used");
        }
        if (!existingCode.equals(code)) {
            throw new IllegalArgumentException("Invalid verification code");
        }
    }
}
