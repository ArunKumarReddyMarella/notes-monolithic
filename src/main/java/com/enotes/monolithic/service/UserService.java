package com.enotes.monolithic.service;

import com.enotes.monolithic.dto.LoginRequest;
import com.enotes.monolithic.dto.LoginResponse;
import com.enotes.monolithic.dto.UserDto;

public interface UserService {

    public Boolean register(UserDto userDto, String url);

    public void verifyUser(String userId, String verificationCode) throws Exception;

    public LoginResponse login(LoginRequest loginRequest);
}
