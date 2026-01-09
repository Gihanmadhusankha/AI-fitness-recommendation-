package com.fitness.ai_service.service;


import com.fitness.ai_service.model.Activity;

public interface ActivityMessageListener {
    void processActivityMessage(Activity activity);
}
