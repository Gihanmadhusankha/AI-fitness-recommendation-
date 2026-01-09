package com.fitness.ai_service.service.impl;

import com.fitness.ai_service.service.GeminiService;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class GeminiServiceImpl implements GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private Client client;

    @PostConstruct
    public void init() {
        // Initialize the client once when the service starts
        // We use the "apiKey" configuration method
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    @Override
    public String getAnswer(String question) {
        try {
            // Use the correct model name (e.g., "gemini-1.5-flash" or "gemini-2.0-flash")
            // Ensure you are using a valid model identifier
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash",
                    question,
                    null // Config can be null if not needed
            );

            return response.text();
        } catch (Exception e) {
            // Log the actual message to help with debugging
            throw new RuntimeException("Gemini API Error: " + e.getMessage(), e);
        }
    }
}