package com.example.activityservice.service.impl;

import com.example.activityservice.dto.ActivityRequest;
import com.example.activityservice.dto.ActivityResponse;
import com.example.activityservice.entity.Activity;
import com.example.activityservice.repo.ActivityRepository;
import com.example.activityservice.service.ActivityService;
import com.example.activityservice.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service

@Slf4j
public class ActivityServiceImpl implements ActivityService {
    private final  ActivityRepository repository;
    private final UserValidationService userValidationService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    public ActivityServiceImpl(ActivityRepository repository, UserValidationService userValidationService, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.userValidationService = userValidationService;
        this.rabbitTemplate = rabbitTemplate;
    }

    //    @Value("${rabbitmq.queue.name}")
//    private String queueName;
    @Override
    public ActivityResponse trackActivity(ActivityRequest request){
         boolean isValidUser=userValidationService.valdateUser(request.getUserId());
           if(!isValidUser){
               throw new RuntimeException("Invalid User" +request.getUserId());
           }
            Activity activity = Activity.builder()
                    .userId(request.getUserId())
                    .type(request.getType())
                    .duration(request.getDuration())
                    .caloriesBurned(request.getCaloriesBurned())
                    .startTime(request.getStartTime())
                    .additionalMetrics(request.getAdditionalMetrics())

                    .build();

            Activity savedActivity = repository.save(activity);

            //public to RabbitMq for AI Processing
          try{
             rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);

          }catch(Exception e){
              log.error("Failed to publish activity to RabbitMQ for activity id: " + savedActivity.getId());

        }
            return mapToResponse(savedActivity);
        }

    @Override
    public List<ActivityResponse> getActivity(String userId) {
        List<Activity> activities=repository.findByUserId(userId);
        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ActivityResponse getActivityById(String activityId) {
        return repository.findById((activityId))
                .map(this::mapToResponse)
                .orElseThrow(()-> new RuntimeException("Activity not found with id" +activityId));

    }

    private ActivityResponse mapToResponse(Activity activity){
        ActivityResponse response=new ActivityResponse();
        response.setId(activity.getId());
        response.setUserId(activity.getUserId());
        response.setType(activity.getType());
        response.setDuration(activity.getDuration());
        response.setCaloriesBurned(activity.getCaloriesBurned());
        response.setAdditionalMetrics(activity.getAdditionalMetrics());
        response.setStartTime(activity.getStartTime());
        response.setCreatedAt(activity.getCreatedAt());
        return response;

    }
}
