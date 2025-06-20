package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.UserDto;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class AuthController {

	@Autowired
	private UserService userService;

	@PostMapping("/")
	public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
		Boolean register = userService.register(userDto);
		if (register) {
			return CommonUtil.createBuildResponseMessage("Register success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Register failed", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
