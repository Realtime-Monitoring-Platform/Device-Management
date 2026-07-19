package com.realtime_monitoring.device_management.dto;

import java.util.UUID;

import com.realtime_monitoring.device_management.entity.DeviceStatus;

public class CreateDeviecRequest {
     private String deviceName;

    private UUID teamId;

    private UUID assignedUserId;

    private String firmwareVersion;

    private String location;

    private DeviceStatus status;
}
