package com.realtime_monitoring.device_management.dto;


import java.util.UUID;

import lombok.Data;

@Data
public class AiCommandRequest {
    private UUID tenantId;
    private String command;
}