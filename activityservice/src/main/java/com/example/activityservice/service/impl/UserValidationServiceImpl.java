package com.example.activityservice.service.impl;

import com.example.activityservice.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j

public class UserValidationServiceImpl implements UserValidationService {
    private final WebClient userServiceWebClient;

    public UserValidationServiceImpl(WebClient userServiceWebClient) {
        this.userServiceWebClient = userServiceWebClient;
    }

    @Override
    public boolean valdateUser(String userId) {
        log.info("Calling User Validation API for userId :",userId);
        try {
            return Boolean.TRUE.equals(userServiceWebClient.get()
                    .uri("/api/users/{userId}/validate", userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("User not found" + userId);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Invalid Request" + userId);
            }
        }
        return false;
    }
    }