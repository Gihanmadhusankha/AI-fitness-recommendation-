package com.fitness.ai_service.service.impl;

import com.fitness.ai_service.model.Recommendation;
import com.fitness.ai_service.repo.RecommendationRepository;
import com.fitness.ai_service.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository) {
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    public List<Recommendation> getRecommendationsByUserId(String userId) {
       return recommendationRepository.findByUserId(userId);
    }

    @Override
    public Recommendation getRecommendationsByActivityId(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found for activityId: " + activityId));
    }
}
