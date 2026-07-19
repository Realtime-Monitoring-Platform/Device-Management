package com.realtime_monitoring.device_management.imp;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.realtime_monitoring.device_management.dto.CreateDeviecRequest;
import com.realtime_monitoring.device_management.dto.DeviceResponse;
import com.realtime_monitoring.device_management.dto.UpdateDeviceRequest;
import com.realtime_monitoring.device_management.service.DeviceService;

public class DeviceServiceimp implements DeviceService{

    @Override
    public DeviceResponse CreateDevice(CreateDeviecRequest createDeviceRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'CreateDevice'");
    }

    @Override
    public void deleteDevice(UUID deviceId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteDevice'");
    }

    @Override
    public DeviceResponse getDEviceById(UUID deviceId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDEviceById'");
    }

    @Override
    public DeviceResponse updateDevice(UUID id, UpdateDeviceRequest updateDeviceRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDevice'");
    }

    @Override
    public Page<DeviceResponse> getAllDevices(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllDevices'");
    }
    
}
