package com.realtime_monitoring.device_management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.realtime_monitoring.device_management.dto.DeviceLog;

public interface DeviceLogService {
 
    void saveLog(DeviceLogMessage message);
 
    Page<DeviceLog> getLogsByDevice(String deviceId, Pageable pageable);
 
    Page<DeviceLog> getLogsByTenant(String tenantId, Pageable pageable);
 
    Page<DeviceLog> getLogsByDeviceAndLevel(String deviceId, String level, Pageable pageable);
}
 