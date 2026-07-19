package com.realtime_monitoring.device_management.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.realtime_monitoring.device_management.dto.CreateDeviecRequest;
import com.realtime_monitoring.device_management.dto.DeviceResponse;
import com.realtime_monitoring.device_management.dto.UpdateDeviceRequest;

public interface DeviceService {
    DeviceResponse CreateDevice(CreateDeviecRequest createDeviceRequest);
    void deleteDevice(UUID deviceId);
    DeviceResponse getDEviceById(UUID deviceId);
    DeviceResponse updateDevice(UUID id, UpdateDeviceRequest updateDeviceRequest);
    Page<DeviceResponse> getAllDevices(Pageable pageable);
}
