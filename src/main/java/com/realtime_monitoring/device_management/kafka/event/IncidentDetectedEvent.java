package com.realtime_monitoring.device_management.kafka.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.realtime_monitoring.device_management.dto.DeviceLog;

public record IncidentDetectedEvent(

        UUID incidentId,

        UUID deviceId,
        UUID tenantId,
        String type,
        
        String severity,
        String message,
        List<DeviceLog> logs,
        Instant timestamp
) {
}