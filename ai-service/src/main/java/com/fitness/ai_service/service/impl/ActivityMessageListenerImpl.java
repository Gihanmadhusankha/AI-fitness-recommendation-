package com.fitness.ai_service.service.impl;

import com.fitness.ai_service.model.Activity;
import com.fitness.ai_service.model.Recommendation;
import com.fitness.ai_service.repo.RecommendationRepository;
import com.fitness.ai_service.service.ActivityAIService;
import com.fitness.ai_service.service.ActivityMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class ActivityMessageListenerImpl implements ActivityMessageListener {
    private final ActivityAIService activityAIService;
    private final RecommendationRepository recommendationRepository;

    public ActivityMessageListenerImpl(ActivityAIService activityAIService, RecommendationRepository recommendationRepository) {
        this.activityAIService = activityAIService;
        this.recommendationRepository = recommendationRepository;
    }

    @RabbitListener(queues ="activity.queue")
    @Override
    public void processActivityMessage(Activity activity) {
        log.info("Received activity message: {}", activity);
//        log.info("Generating recommendation for activity: {}", activityAIService.generateRecommendation(activity));
        Recommendation recommendation = activityAIService.generateRecommendation(activity);
        recommendationRepository.save(recommendation);
    }
}
