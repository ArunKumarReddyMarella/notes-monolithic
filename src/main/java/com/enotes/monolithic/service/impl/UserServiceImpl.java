package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.dto.EmailRequest;
import com.enotes.monolithic.dto.UserDto;
import com.enotes.monolithic.entity.AccountStatus;
import com.enotes.monolithic.entity.Role;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.exception.ResourceNotFoundException;
import com.enotes.monolithic.repository.RoleRepository;
import com.enotes.monolithic.repository.UserRepository;
import com.enotes.monolithic.service.UserService;

import com.enotes.monolithic.util.Validation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private Validation validation;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private EmailService emailService;

    @Override
    public Boolean register(UserDto userDto, String url) {

        validation.userValidation(userDto);
        User user = mapper.map(userDto, User.class);

        setRole(userDto, user);
        setDefaultAccountStatus(user);

        User savedUser = userRepo.save(user);
        if (!ObjectUtils.isEmpty(savedUser)) {
            sendRegistrationEmail(savedUser, url);
            return true;
        }
        return false;
    }

    @Override
    public void verifyUser(String userId, String verificationCode) throws Exception {
        User user = userRepo.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User Not found & Id invalid"));

        validation.verificationCodeValidation(user,verificationCode);

        setVerifiedAccountStatus(user);
        userRepo.save(user);
    }

    private void setVerifiedAccountStatus(User user) {
        user.getAccountStatus().setIsActive(true);
        user.getAccountStatus().setVerificationCode(null);
    }

    private void setDefaultAccountStatus(User user) {
        AccountStatus accountStatus = AccountStatus.builder()
                .isActive(false)
                .verificationCode(UUID.randomUUID().toString())
                .build();
        user.setAccountStatus(accountStatus);
    }

    private void setRole(UserDto userDto, User user) {
        List<Integer> reqRoleId = userDto.getRoles().stream().map(UserDto.RoleDto::getId).toList();
        List<Role> roles = roleRepo.findAllById(reqRoleId);
        user.setRoles(roles);
    }

    private void sendRegistrationEmail(User user, String url) {
        validation.emailValidation(user.getEmail());

        EmailRequest emailRequest = EmailRequest.builder()
                .to(user.getEmail())
                .title("Account Creating Confirmation")
                .subject("Welcome to Enotes!")
                .message("Hi " + user.getFirstName() + ", welcome to Enotes!")
                .build();

        emailService.sendEmail(user, emailRequest, url);
    }
}
