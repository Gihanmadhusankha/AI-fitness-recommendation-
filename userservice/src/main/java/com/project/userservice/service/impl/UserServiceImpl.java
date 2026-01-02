package com.project.userservice.service.impl;

import com.project.userservice.repo.UserRepository;
import com.project.userservice.dto.RegisterRequest;
import com.project.userservice.dto.UserResponse;
import com.project.userservice.entity.User;
import com.project.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository repository;
    @Override
    public UserResponse register(RegisterRequest register) {
        if(repository.existsByEmail(register.getEmail())){
            throw new RuntimeException("Email Already exist");
        }
        User user=new User();
        user.setEmail(register.getEmail());
        user.setPassword(register.getPassword());
        user.setFirstName(register.getFirstName());
        user.setLastName(register.getLastName());

      User savedUser  =repository.save(user);
        UserResponse response=new UserResponse();
        response.setEmail(savedUser.getEmail());
        response.setPassword(savedUser.getPassword());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());

        return response;

    }


    @Override
    public UserResponse getUserProfile(String userId) {
        User user =repository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));
        UserResponse response=new UserResponse();
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        return response;
    }

    @Override
    public Boolean existByUserId(String userId) {
        return repository.existsById(userId);
    }
}
