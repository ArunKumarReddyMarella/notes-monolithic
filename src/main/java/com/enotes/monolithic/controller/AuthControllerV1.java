package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.LoginRequest;
import com.enotes.monolithic.dto.LoginResponse;
import com.enotes.monolithic.dto.UserRequest;
import com.enotes.monolithic.service.AuthService;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin
@Tag(name = "Authentication", description = "User Authentication APIs")
@RestController
@RequestMapping("/api/v1/auth/user")
public class AuthControllerV1 {
    private static final Logger logger = LoggerFactory.getLogger(AuthControllerV1.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Register Success"),
            @ApiResponse(responseCode = "500", description = "Internal Server error"),
            @ApiResponse(responseCode = "400", description = "Bad Request") })
    @Operation(summary = "User Register Endpoint", tags = { "Authentication" })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest, HttpServletRequest request) throws Exception {
        logger.info("Received user request for Registration : {}", userRequest);
        String url = CommonUtil.getUrl(request);
        Boolean register = authService.register(userRequest, url);
        if (Boolean.TRUE.equals(register)) {
            return CommonUtil.createBuildResponseMessage("Registration success", HttpStatus.CREATED);
        }
        return CommonUtil.createErrorResponseMessage("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

//	@GetMapping("/verify-account")
//	public ResponseEntity<?> verifyUser(@RequestParam String userId, @RequestParam String verificationCode) throws Exception {
//		authService.verifyUser(userId, verificationCode);
//		return CommonUtil.createBuildResponseMessage("Verification success", HttpStatus.OK);
//	}

    @Operation(summary = "Verification user account", tags = {
            "Home" }, description = "User Account verification after register account")
    @GetMapping("/verify-account")
    public ModelAndView verifyUser(@RequestParam String userId, @RequestParam String verificationCode) throws Exception {
        logger.info("Received user request for Verification : {}", userId);
        try {
            authService.verifyUser(userId, verificationCode);
            return new ModelAndView("account-verification-success");
        } catch (Exception e) {
            logger.error("Failed to verify user: {}", userId, e);
            return new ModelAndView("account-verification-failure");
        }
    }

    @Operation(summary = "User Login Endpoint", tags = { "Authentication", "Home"})
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
        logger.info("Received user request for Login : {}", loginRequest);
        LoginResponse loginResponse = authService.login(loginRequest);
        if (ObjectUtils.isEmpty(loginResponse)) {
            return CommonUtil.createErrorResponseMessage("invalid credential", HttpStatus.BAD_REQUEST);
        }
        return CommonUtil.createBuildResponse(loginResponse, HttpStatus.OK);
    }

    @Operation(summary = "Send Email for Password Reset", tags = {
            "Home" }, description = "User Can send Email for password reset")
    @GetMapping("/send-reset-password-email")
    public ResponseEntity<?> sendEmailForResetPassword(@RequestParam String email, HttpServletRequest request) throws Exception {
        logger.info("Received user request for Reset Password : {}", email);
        String baseUrl = CommonUtil.getUrl(request);
        userService.sendEmailForResetPassword(email, baseUrl);
        return CommonUtil.createBuildResponseMessage("Email sent successfully", HttpStatus.OK);
    }

    @Operation(summary = "Verification password link", tags = {
            "Home" }, description = "User verification password link")
    @GetMapping("/verify-reset-password-code")
    public ModelAndView verifyResetPassword(@RequestParam Integer userId, @RequestParam String code, HttpServletRequest request) throws Exception {
        logger.info("Received user request for Verify Reset Password : {}", userId);
        try {
            String baseUrl = CommonUtil.getUrl(request);
            userService.verifyResetPasswordCode(userId, code);

            // Show the password reset form with the userId and code
            ModelAndView modelAndView = new ModelAndView("reset-password-form");
            modelAndView.addObject("userId", userId);
            modelAndView.addObject("baseUrl", baseUrl);
            return modelAndView;
        } catch (Exception e) {
            logger.error("Failed to verify reset password code: {}", userId, e);
            return new ModelAndView("reset-password-verification-failure");
        }
    }

    @Operation(summary = "Reset Password", tags = { "Home" }, description = "User Can changes Password")
    @GetMapping("/reset-password/{userId}")
    public ModelAndView resetPassword(@PathVariable Integer userId, @RequestParam String newPassword) throws Exception {
        logger.info("Received user request for Reset Password : {}", userId);
        try {
            // Reset the password
            userService.resetPassword(userId, newPassword);
            return new ModelAndView("reset-password-success");
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("reset-password-form");
            modelAndView.addObject("userId", userId);
            modelAndView.addObject("error", "Failed to reset password. Please try again.");
            logger.error("Failed to reset password: {}", userId, e);
            return modelAndView;
        }
    }

}
