package com.enotes.monolithic.controller;

import com.enotes.monolithic.dto.UserResponse;
import com.enotes.monolithic.entity.User;
import com.enotes.monolithic.util.CommonUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserControllerV1 {

    @Autowired
    private ModelMapper mapper;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        User loggedUser = CommonUtil.getLoggedInUser();
        UserResponse userResponse = mapper.map(loggedUser, UserResponse.class);
        return CommonUtil.createBuildResponse(userResponse, HttpStatus.OK);
    }
}
