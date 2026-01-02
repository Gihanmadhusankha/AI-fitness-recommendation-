package com.example.activityservice.dto;

import com.example.activityservice.enums.ActivityType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
@Data
public class ActivityRequest {
    private String userId;

    @JsonProperty("activityType")
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String,Object> additionalMetrics;
}
