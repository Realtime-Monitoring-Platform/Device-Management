package com.realtime_monitoring.device_management.dto;

import lombok.Data;

@Data
public class ProvisionResponse {
    private String deviceId;
    private String clientCerticate;
    private String caCertificate;
}
