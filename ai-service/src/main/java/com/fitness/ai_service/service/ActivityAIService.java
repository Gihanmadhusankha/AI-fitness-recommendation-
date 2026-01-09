package com.fitness.ai_service.service;

import com.fitness.ai_service.model.Activity;
import com.fitness.ai_service.model.Recommendation;

public interface ActivityAIService {
    Recommendation generateRecommendation(Activity activity);
}
