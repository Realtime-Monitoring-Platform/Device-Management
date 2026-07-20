package com.realtime_monitoring.device_management.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.realtime_monitoring.device_management.entity.DeviceStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeviceRequest {

    private UUID tenantId;
    private UUID teamId;
    private String deviceName;
    private String serialNumber;
    private String hostname;
    private String manufacturer;
    private String model;

    private String macAddress;

    private String ipAddress;

    private String firmwareVersion;

    private String agentVersion;

    private DeviceStatus status;


    private LocalDateTime lastSeen;


}
