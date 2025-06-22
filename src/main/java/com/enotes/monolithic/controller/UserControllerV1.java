package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.PasswordChngRequest;
import com.enotes.monolithic.dto.UserResponse;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.service.UserService;
import com.enotes.monolithic.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserControllerV1 {

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        User loggedUser = CommonUtil.getLoggedInUser();
        UserResponse userResponse = mapper.map(loggedUser, UserResponse.class);
        return CommonUtil.createBuildResponse(userResponse, HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChngRequest passwordChngRequest) {
        userService.changePassword(passwordChngRequest);
        return CommonUtil.createBuildResponseMessage("Password changed successfully", HttpStatus.OK);
    }

}
