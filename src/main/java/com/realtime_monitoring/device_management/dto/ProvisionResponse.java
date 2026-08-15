package com.realtime_monitoring.device_management.dto;

import lombok.Data;

@Data
public class ProvisionResponse {
    private String tenantId;
    private String deviceId;
    private String clientCertificate;
    private String caCertificate;
}
