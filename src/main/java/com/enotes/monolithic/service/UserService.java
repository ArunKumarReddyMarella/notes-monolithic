package com.enotes.monolithic.service;

import com.enotes.monolithic.dto.PasswordChngRequest;

public interface UserService {
    public void changePassword(PasswordChngRequest passwordChngRequest);
}
