package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.UserDto;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/v1/user")
public class AuthController {

	@Autowired
	private UserService userService;

	@PostMapping("/")
	public ResponseEntity<?> registerUser(@RequestBody UserDto userDto, HttpServletRequest request) throws Exception {
		String url =  CommonUtil.getUrl(request);
		Boolean register = userService.register(userDto, url);
		if (Boolean.TRUE.equals(register)) {
			return CommonUtil.createBuildResponseMessage("Registration success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
	}

//	@GetMapping("/verify-account")
//	public ResponseEntity<?> verifyUser(@RequestParam String userId, @RequestParam String verificationCode) throws Exception {
//		userService.verifyUser(userId, verificationCode);
//		return CommonUtil.createBuildResponseMessage("Verification success", HttpStatus.OK);
//	}

	@GetMapping("/verify-account")
    public ModelAndView verifyUser(@RequestParam String userId, @RequestParam String verificationCode) throws Exception {
		try {
			userService.verifyUser(userId, verificationCode);
			return new ModelAndView("verification-success");
		} catch (Exception e) {
			return new ModelAndView("verification-failure");
		}
	}

}
