package com.realtime_monitoring.device_management.dto;

import lombok.Data;

import java.util.List;

@Data
public class AIAnalysisResponse {

    private String severity;
    private String problem;
    private String rootCause;
    private double confidence;
    private String impact;
    private List<Recommendation> recommendations;

    
    @Data
    public static class Recommendation {

        private int priority;
        private String action;
        private String command;
    }
}