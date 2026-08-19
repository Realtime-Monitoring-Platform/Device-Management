package com.realtime_monitoring.device_management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public  class LogEntry {
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