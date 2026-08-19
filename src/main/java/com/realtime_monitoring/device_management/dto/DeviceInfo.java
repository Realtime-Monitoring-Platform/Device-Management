package com.realtime_monitoring.device_management.dto;

import lombok.Data;

@Data
public class DeviceInfo {
    String hostname;
    String ipAddress;
    String macAddress;
    String osName;
    String osVersion;
    String kernelVersion;
    Long cpuCount;
    Long totalMemoryKb;
}
