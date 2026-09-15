package com.realtime_monitoring.device_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentRequest {

    private String deviceId;
    private String tenantId;

    
    private String type;
    private String severity;

    private String message;

    private List<DeviceLog> logs;
}