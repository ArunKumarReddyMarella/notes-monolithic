package com.enotes.monolithic.service;

import com.enotes.monolithic.dto.UserDto;
import com.enotes.monolithic.exception.ResourceNotFoundException;

public interface UserService {

	public Boolean register(UserDto userDto, String url);

    void verifyUser(String userId, String verificationCode) throws Exception;
}
