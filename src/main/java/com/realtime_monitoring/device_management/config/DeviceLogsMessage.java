package com.realtime_monitoring.device_management.config;


import lombok.Data;

import java.util.List;

import com.realtime_monitoring.device_management.service.DeviceLogMessage;

@Data
public class DeviceLogsMessage {

    private String deviceId;

    private String tenantId;

    private List<DeviceLogMessage> logs;
}