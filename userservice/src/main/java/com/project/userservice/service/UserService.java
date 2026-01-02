package com.project.userservice.service;

import com.project.userservice.dto.RegisterRequest;
import com.project.userservice.dto.UserResponse;
import com.project.userservice.entity.User;

import java.util.List;

public interface UserService {
    UserResponse register(RegisterRequest register);
    UserResponse getUserProfile(String userId);

    Boolean existByUserId(String userId);
}
