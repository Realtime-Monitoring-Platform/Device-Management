package com.realtime_monitoring.device_management.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class CommandApprovalRequest {

    private UUID incidentId;
    private UUID deviceId;

    private UUID tenantId;
    
    private int recommendationPriority;
    private String command;
    private UUID requestedBy;
}