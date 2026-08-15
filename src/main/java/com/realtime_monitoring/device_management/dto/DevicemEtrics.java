package com.realtime_monitoring.device_management.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class DevicemEtrics {
    private UUID device_id;
    private UUID tenant_id;
    private double cpu;
    private double ram;
}
