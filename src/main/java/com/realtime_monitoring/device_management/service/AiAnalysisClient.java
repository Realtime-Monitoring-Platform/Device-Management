package com.realtime_monitoring.device_management.service;

import com.realtime_monitoring.device_management.dto.AIAnalysisResponse;
import com.realtime_monitoring.device_management.dto.IncidentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AiAnalysisClient {

    private final WebClient webClient;

    @Value("${ai.service.url}")
    private String aiServiceUrl;

    
    public AIAnalysisResponse analyze(IncidentRequest incident) {

        return webClient
                .post()
                .uri(aiServiceUrl + "/analyze")
                .bodyValue(incident)
                .retrieve()
                .bodyToMono(AIAnalysisResponse.class)
                .block();
    }
}