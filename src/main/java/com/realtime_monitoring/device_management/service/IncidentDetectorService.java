package com.realtime_monitoring.device_management.service;

import com.realtime_monitoring.device_management.dto.AIAnalysisResponse;
import com.realtime_monitoring.device_management.dto.DeviceLog;
import com.realtime_monitoring.device_management.dto.IncidentRequest;
import com.realtime_monitoring.device_management.kafka.event.IncidentDetectedEvent;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentDetectorService {

    private final DeviceLogService deviceLogService;
    private final AiAnalysisClient aiAnalysisClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void checkLogIncident(DeviceLog log) {

        if (log == null || log.getLevel() == null) {
            return;
        }

        String level = log.getLevel().toUpperCase();

        if ("ERROR".equals(level) || "FATAL".equals(level)) {

            System.out.println("=====================================");
            System.out.println("incident detactedd");
            System.out.println("device:::" + log.getDeviceId());
            System.out.println("level::::" + log.getLevel());
            System.out.println("message:::" + log.getMessage());
            System.out.println("=====================================");

            List<DeviceLog> context = deviceLogService.getRecentLogs(log.getDeviceId(), 5);

            System.out.println("========== ELASTICSEARCH CONTEXT ==========");

            context.forEach(log1 -> {
                System.out.println(
                        "[" + log1.getLevel() + "] " +
                                log1.getMessage());
            });

            System.out.println("===========================================");

            IncidentRequest incident = new IncidentRequest(
                    log.getDeviceId(),
                    log.getTenantId(),
                    "APPLICATION_ERROR",
                    "HIGH",
                    log.getMessage(),
                    context);

            IncidentDetectedEvent event = new IncidentDetectedEvent(
                    UUID.randomUUID(),
                    UUID.fromString(log.getDeviceId()),
                    UUID.fromString(log.getTenantId()),
                    "APPLICATION_ERROR",
                    "HIGH",
                    log.getMessage(),
                    context,
                    Instant.now());

            kafkaTemplate.send(
                    "incident-events",
                    log.getDeviceId(),
                    event);
         //   AIAnalysisResponse analysis = aiAnalysisClient.analyze(incident);
            System.out.println("AI service returned successfully//////////////////////////////");
            System.out.println("========================================");
            System.out.println("🤖 AI ANALYSIS");

            // System.out.println("Severity: " + analysis.getSeverity());
            // System.out.println("Problem: " + analysis.getProblem());
            // System.out.println("Root Cause: " + analysis.getRootCause());
            // System.out.println("Confidence: " + analysis.getConfidence());
            // System.out.println("Impact: " + analysis.getImpact());
            // if (analysis.getRecommendations() != null) {
            //     analysis.getRecommendations().forEach(rec -> {
            //         System.out.println(
            //                 "Priority " + rec.getPriority() +
            //                         ": " + rec.getAction());

            //         if (rec.getCommand() != null) {
            //             System.out.println("Command: " + rec.getCommand());
            //         }
            //     });
            // }

            System.out.println("========================================");
        }
    }
}