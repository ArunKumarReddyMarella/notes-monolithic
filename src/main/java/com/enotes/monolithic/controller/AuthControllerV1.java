package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.LoginRequest;
import com.enotes.monolithic.dto.LoginResponse;
import com.enotes.monolithic.dto.UserRequest;
import com.enotes.monolithic.service.AuthService;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/v1/auth/user")
public class AuthControllerV1 {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest, HttpServletRequest request) throws Exception {
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

    @GetMapping("/verify-account")
    public ModelAndView verifyUser(@RequestParam String userId, @RequestParam String verificationCode) throws Exception {
        try {
            authService.verifyUser(userId, verificationCode);
            return new ModelAndView("account-verification-success");
        } catch (Exception e) {
            return new ModelAndView("account-verification-failure");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {

        LoginResponse loginResponse = authService.login(loginRequest);
        if (ObjectUtils.isEmpty(loginResponse)) {
            return CommonUtil.createErrorResponseMessage("invalid credential", HttpStatus.BAD_REQUEST);
        }
        return CommonUtil.createBuildResponse(loginResponse, HttpStatus.OK);
    }

    @GetMapping("/send-reset-password-email")
    public ResponseEntity<?> sendEmailForResetPassword(@RequestParam String email, HttpServletRequest request) throws Exception {
        String baseUrl = CommonUtil.getUrl(request);
        userService.sendEmailForResetPassword(email, baseUrl);
        return CommonUtil.createBuildResponseMessage("Email sent successfully", HttpStatus.OK);
    }

    @GetMapping("/verify-reset-password-code")
    public ModelAndView verifyResetPassword(@RequestParam Integer userId, @RequestParam String code, HttpServletRequest request) throws Exception {
        try {
            String baseUrl = CommonUtil.getUrl(request);
            userService.verifyResetPasswordCode(userId, code);

            // Show the password reset form with the userId and code
            ModelAndView modelAndView = new ModelAndView("reset-password-form");
            modelAndView.addObject("userId", userId);
            modelAndView.addObject("baseUrl", baseUrl);
            return modelAndView;
        } catch (Exception e) {
            return new ModelAndView("reset-password-verification-failure");
        }
    }

    @GetMapping("/reset-password/{userId}")
    public ModelAndView resetPassword(@PathVariable Integer userId, @RequestParam String newPassword) throws Exception {
        try {
            // Reset the password
            userService.resetPassword(userId, newPassword);
            return new ModelAndView("reset-password-success");
        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView("reset-password-form");
            modelAndView.addObject("userId", userId);
            modelAndView.addObject("error", "Failed to reset password. Please try again.");
            return modelAndView;
        }
    }

}
