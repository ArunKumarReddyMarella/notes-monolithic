package com.enotes.monolithic.service.impl;

import com.enotes.monolithic.config.security.CustomUserDetails;
import com.enotes.monolithic.dto.*;
import com.enotes.monolithic.entity.AccountStatus;
import com.enotes.monolithic.entity.Role;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.exception.ResourceNotFoundException;
import com.enotes.monolithic.repository.RoleRepository;
import com.enotes.monolithic.repository.UserRepository;
import com.enotes.monolithic.service.EmailService;
import com.enotes.monolithic.service.JWTService;
import com.enotes.monolithic.service.AuthService;
import com.enotes.monolithic.util.Validation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Override
    public Boolean register(UserRequest userRequest, String url) {

        validation.userValidation(userRequest);
        User user = mapper.map(userRequest, User.class);

        setRole(userRequest, user);
        setDefaultAccountStatus(user);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

        validation.verificationCodeValidation(user, verificationCode);

        setVerifiedAccountStatus(user);
        userRepo.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(customUserDetails.getUser());
            return LoginResponse.builder()
                    .token(token)
                    .user(mapper.map(customUserDetails.getUser(), UserResponse.class))
                    .build();
        }
        return null;
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

    private void setRole(UserRequest userRequest, User user) {
        List<Integer> reqRoleId = userRequest.getRoles().stream().map(UserRequest.RoleDto::getId).toList();
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
