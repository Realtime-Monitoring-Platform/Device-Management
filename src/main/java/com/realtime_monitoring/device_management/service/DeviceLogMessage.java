package com.realtime_monitoring.device_management.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DeviceLogMessage {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("tenant_id")
    private String tenantId;
    private String level;
    private String service;
    private String source;
    private String message;
    private long timestamp;
}
