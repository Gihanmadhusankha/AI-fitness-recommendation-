package com.fitness.ai_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.ai_service.model.Activity;
import com.fitness.ai_service.model.Recommendation;
import com.fitness.ai_service.service.ActivityAIService;
import com.fitness.ai_service.service.GeminiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ActivityAIServiceImpl implements ActivityAIService {

    private final GeminiService geminiService;
    private final ObjectMapper mapper = new ObjectMapper(); // Reuse mapper

    public ActivityAIServiceImpl(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @Override
    public Recommendation generateRecommendation(Activity activity) {
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getAnswer(prompt);
        log.info("AI Response: {}", aiResponse);

        return processAiResponse(activity, aiResponse);
    }

    private Recommendation processAiResponse(Activity activity, String aiResponse) {
        try {
            JsonNode rootNode = mapper.readTree(aiResponse);

            // Navigate to the actual text content from Gemini
            String rawJson = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // Clean Markdown code blocks if present
            String jsonContent = rawJson.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode analysisJson = mapper.readTree(jsonContent);

            // Build the main recommendation string from the "analysis" object
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories Burned");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate");

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .activityType(activity.getType())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(extractList(analysisJson.path("improvements"), "area", "recommendation"))
                    .suggestions(extractList(analysisJson.path("suggestions"), "workout", "description"))
                    .safety(extractList(analysisJson.path("safety"), null, "item"))
                    .build();

        } catch (Exception e) {
            log.error("Error parsing AI response: {}", e.getMessage());
            return createDefaultRecommendation(activity);
        }
    }

    private List<String> extractList(JsonNode node, String key1, String key2) {
        List<String> list = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(item -> {
                if (key1 != null) {
                    list.add(String.format("%s: %s", item.path(key1).asText(), item.path(key2).asText()));
                } else {
                    list.add(item.path(key2).asText());
                }
            });
        }
        return list.isEmpty() ? Collections.emptyList() : list;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        JsonNode sectionNode = analysisNode.path(key);
        if (!sectionNode.isMissingNode()) {
            fullAnalysis.append(prefix).append(":\n");
            if (sectionNode.isObject()) {
                sectionNode.fields().forEachRemaining(entry ->
                        fullAnalysis.append("- ").append(entry.getValue().asText()).append("\n")
                );
            } else {
                fullAnalysis.append("- ").append(sectionNode.asText()).append("\n");
            }
            fullAnalysis.append("\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        Map<String, Object> metrics = activity.getAdditionalMetrics() != null
                ? activity.getAdditionalMetrics() : Collections.emptyMap();

        return String.format(
                "You are a fitness coaching expert. Analyze this %s activity: " +
                        "Duration: %d min, Calories: %d, Heart Rate: %s. " +
                        "Return ONLY a JSON object with this exact structure: " +
                        "{" +
                        "  \"analysis\": {\"overall\": \"...\", \"pace\": \"...\", \"caloriesBurned\": \"...\", \"heartRate\": \"...\"}," +
                        "  \"improvements\": [{\"area\": \"...\", \"recommendation\": \"...\"}]," +
                        "  \"suggestions\": [{\"workout\": \"...\", \"description\": \"...\"}]," +
                        "  \"safety\": [{\"item\": \"...\"}]" +
                        "}",
                activity.getType(), activity.getDuration(), activity.getCaloriesBurned(),
                metrics.getOrDefault("averageHeartRate", "N/A")
        );
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(activity.getType())
                .recommendation("Analysis currently unavailable.")
                .improvements(Collections.emptyList())
                .suggestions(Collections.emptyList())
                .safety(Collections.emptyList())
                .build();
    }
}