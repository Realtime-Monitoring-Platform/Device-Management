package com.realtime_monitoring.device_management.imp;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.realtime_monitoring.device_management.dto.CreateDeviecRequest;
import com.realtime_monitoring.device_management.dto.DeviceResponse;
import com.realtime_monitoring.device_management.dto.UpdateDeviceRequest;
import com.realtime_monitoring.device_management.entity.Device;
import com.realtime_monitoring.device_management.mapper.DeviceMapper;
import com.realtime_monitoring.device_management.repository.DeviceRepository;
import com.realtime_monitoring.device_management.service.DeviceService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service

public class DeviceServiceimp implements DeviceService{

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    @Override
    public DeviceResponse CreateDevice(CreateDeviecRequest createDeviceRequest) {
        Device device= deviceMapper.toEntity(createDeviceRequest);
        return deviceMapper.toResponse(this.deviceRepository.save(device));
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
