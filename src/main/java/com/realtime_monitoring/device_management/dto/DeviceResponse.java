package com.realtime_monitoring.device_management.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.realtime_monitoring.device_management.entity.DeviceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;
    private UUID teamId;
    private UUID assignedUserId;

    @Column(nullable = false)
    private String deviceName;

    private String model;

    private String manufacturer;

    @Column(nullable = false)
    private String hostname;

    private String ipAddress;
    private String macAddress;
    private String location;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private LocalDateTime lastSeen;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
