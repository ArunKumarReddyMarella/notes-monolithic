package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.PasswordChngRequest;
import com.enotes.monolithic.dto.UserResponse;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.enotes.monolithic.util.Constants.ROLE_ADMIN_USER;

@CrossOrigin
@Tag(name = "User", description = "User Operation APIs")
@RestController
@RequestMapping("/api/v1/user")
public class UserControllerV1 {
    private static final Logger logger = LoggerFactory.getLogger(UserControllerV1.class);

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private UserService userService;

    @Operation(summary = "Get User Profile", tags = { "User" }, description = "Get User Profile")
    @GetMapping("/profile")
    @PreAuthorize(ROLE_ADMIN_USER)
    public ResponseEntity<?> getProfile() {
        logger.info("Request to get Logged in user profile");
        User loggedUser = CommonUtil.getLoggedInUser();
        UserResponse userResponse = mapper.map(loggedUser, UserResponse.class);
        return CommonUtil.createBuildResponse(userResponse, HttpStatus.OK);
    }

    @Operation(summary = "User Account Password Change", tags = {
            "User" }, description = "User Account Password Change")
    @PostMapping("/change-password")
    @PreAuthorize(ROLE_ADMIN_USER)
    public ResponseEntity<?> changePassword(@RequestBody PasswordChngRequest passwordChngRequest) {
        logger.info("Request to change password");
        userService.changePassword(passwordChngRequest);
        return CommonUtil.createBuildResponseMessage("Password changed successfully", HttpStatus.OK);
    }

}
