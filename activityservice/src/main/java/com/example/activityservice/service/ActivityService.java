package com.example.activityservice.service;

import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;

import java.util.List;

public interface ActivityService {
    ActivityResponse trackActivity(ActivityRequest request);

    List<ActivityResponse> getActivity(String userId);

    ActivityResponse getActivityById(String activityId);
}
