package com.realtime_monitoring.device_management.dto;

import lombok.Data;

@Data
public class CommandResult {
    private String commandId;
    private String deviceId;
    private String status;
    private String stdout;

    private int exitCode;
}
