package com.realtime_monitoring.device_management.event;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.realtime_monitoring.device_management.entity.DeviceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

public record DeviceCreatedEvent(
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

        LocalDateTime lastSeen

        ) {
}
