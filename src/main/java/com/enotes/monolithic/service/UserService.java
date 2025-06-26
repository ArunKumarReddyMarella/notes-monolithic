package com.enotes.monolithic.service;

import com.enotes.monolithic.dto.PasswordChngRequest;

public interface UserService {
    public void changePassword(PasswordChngRequest passwordChngRequest);

    public void resetPassword(Integer userId, String newPassword) throws Exception;

    void sendEmailForResetPassword(String email,String baseUrl) throws Exception;

    void verifyResetPasswordCode(Integer userId,String code) throws Exception;
}
