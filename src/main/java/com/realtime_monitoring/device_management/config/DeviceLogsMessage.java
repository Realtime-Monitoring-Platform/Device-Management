package com.realtime_monitoring.device_management.config;


import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.realtime_monitoring.device_management.service.DeviceLogMessage;

@Data
public class DeviceLogsMessage {
    @JsonProperty("device_id")
    private String deviceId;
    @JsonProperty("tenant_id")
    private String tenantId;

    private List<DeviceLogMessage> logs;
}