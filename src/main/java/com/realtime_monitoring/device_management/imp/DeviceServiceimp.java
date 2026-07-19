package com.realtime_monitoring.device_management.imp;

import java.util.Optional;
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

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
@Transactional
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
        this.deviceRepository.deleteById(deviceId);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceResponse getDEviceById(UUID deviceId) {
        Optional<Device> device = this.deviceRepository.findById(deviceId);
        if(device.isEmpty()){
            throw new RuntimeException("device not found");
        }
        DeviceResponse deviceReponse = this.deviceMapper.toResponse(device.get());
        return deviceReponse;
    }

    @Override
    public DeviceResponse updateDevice(UUID id, UpdateDeviceRequest updateDeviceRequest) {
        Optional<Device> device = this.deviceRepository.findById(id);
        if(device.isEmpty()){
            throw new RuntimeException("device not found");
        }
        Device updatedDevice = deviceMapper.updateDeviceFromRequest(updateDeviceRequest, device.get());
        return deviceMapper.toResponse(this.deviceRepository.save(updatedDevice));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceResponse> getAllDevices(Pageable pageable) {
        Page<DeviceResponse> devices = this.deviceRepository.findAll(pageable).map(deviceMapper::toResponse);
        return devices;
    }
    
}
