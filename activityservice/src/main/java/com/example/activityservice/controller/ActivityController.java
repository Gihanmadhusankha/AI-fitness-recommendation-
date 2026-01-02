package com.example.activityservice.controller;

import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import com.example.activityservice.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityResponse>trackActivity(@RequestBody ActivityRequest request){
        return ResponseEntity.ok(activityService.trackActivity(request));

    }
    @GetMapping
    public ResponseEntity<List<ActivityResponse>>getUserActivities(@RequestHeader ("X-User-ID") String userId){
        return ResponseEntity.ok(activityService.getActivity(userId));

    }
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse>getUserActivityById(@PathVariable String activityId){
        return ResponseEntity.ok(activityService.getActivityById(activityId));

    }
}
