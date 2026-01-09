package com.fitness.ai_service.service;

import com.fitness.ai_service.model.Recommendation;

import java.util.List;
import java.util.Optional;

public interface RecommendationService {
    List<Recommendation> getRecommendationsByUserId(String userId);

    Recommendation getRecommendationsByActivityId(String activityId);
}
