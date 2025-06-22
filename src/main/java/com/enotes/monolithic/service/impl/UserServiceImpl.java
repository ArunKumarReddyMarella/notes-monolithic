package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.PasswordChngRequest;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.repository.UserRepository;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(PasswordChngRequest passwordChngRequest) {
        User user = CommonUtil.getLoggedInUser();
        String currentPassword = passwordChngRequest.getCurrentPassword();
        String newPassword = passwordChngRequest.getNewPassword();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}
