package com.realtime_monitoring.device_management.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.realtime_monitoring.device_management.entity.DeviceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeviecRequest {
    private String deviceName;

    private UUID teamId;
    private UUID assignedUserId;
    // private String firmwareVersion;
    private UUID tenantId;
    // private String model;
    // private String manufacturer;
    
    private DeviceStatus status;

}
