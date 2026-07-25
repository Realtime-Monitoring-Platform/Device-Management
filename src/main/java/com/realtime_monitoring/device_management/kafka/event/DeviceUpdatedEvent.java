package com.realtime_monitoring.device_management.kafka.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.realtime_monitoring.device_management.entity.DeviceStatus;

public record DeviceUpdatedEvent(
        UUID id,
        DomainEvent event,
        UUID assignedUserId,
        String deviceName,
        String model,
        String manufacturer,
        String hostname,
        String ipAddress,
        String macAddress,
        String location,
        DeviceStatus status,
        LocalDateTime lastSeen,
        UUID tenantId,
        UUID teamId
) {
}
