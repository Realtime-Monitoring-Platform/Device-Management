package com.realtime_monitoring.device_management.imp;

import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.common.config.types.Password;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.realtime_monitoring.device_management.dto.CreateDeviecRequest;
import com.realtime_monitoring.device_management.dto.DeviceResponse;
import com.realtime_monitoring.device_management.dto.UpdateDeviceRequest;
import com.realtime_monitoring.device_management.entity.Device;
import com.realtime_monitoring.device_management.exceptions.DeviceNotFoundException;
import com.realtime_monitoring.device_management.kafka.DeviceProducer;
import com.realtime_monitoring.device_management.mapper.DeviceMapper;
import com.realtime_monitoring.device_management.repository.DeviceRepository;
import com.realtime_monitoring.device_management.service.DeviceService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class DeviceServiceimp implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    private final DeviceProducer deviceProducer;

    @Override
    public DeviceResponse CreateDevice(CreateDeviecRequest createDeviceRequest) {
        Device device = deviceMapper.toEntity(createDeviceRequest);
        String generatedPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String GeneratedId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        device.setMqttPassword(generatedPassword);
        device.setMqttUsername(GeneratedId);
        System.out.println("Generated Password: " + generatedPassword);
        String hashedPassword = org.springframework.security.crypto.bcrypt.BCrypt.hashpw(generatedPassword,
                org.springframework.security.crypto.bcrypt.BCrypt.gensalt());
        device.setMqttHashPassword(hashedPassword);
        Device savedDevice = deviceRepository.save(device);

        deviceProducer.sendDeviceCreation(savedDevice);
        return deviceMapper.toResponse(savedDevice);
    }

    @Override
    public void deleteDevice(UUID deviceId) {
        this.deviceRepository.deleteById(deviceId);
        this.deviceProducer.sendDeviceDeleted(deviceId);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceResponse getDEviceById(UUID deviceId) {
        Optional<Device> device = this.deviceRepository.findById(deviceId);
        if (device.isEmpty()) {
            throw new DeviceNotFoundException("device with ID " + deviceId + " not found");
        }
        DeviceResponse deviceReponse = this.deviceMapper.toResponse(device.get());
        return deviceReponse;
    }

    @Override
    public DeviceResponse updateDevice(UUID id, UpdateDeviceRequest updateDeviceRequest) {
        Device device = this.deviceRepository.findById(id).orElseThrow(
                () -> new DeviceNotFoundException("device with ID " + id + " not found"));

        deviceMapper.updateDeviceFromRequest(updateDeviceRequest, device);
        Device updatedDevice = this.deviceRepository.save(device);
        deviceProducer.sendDeviceUpdate(updatedDevice);
        return deviceMapper.toResponse(updatedDevice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceResponse> getAllDevices(Pageable pageable) {
        Page<DeviceResponse> devices = this.deviceRepository.findAll(pageable).map(deviceMapper::toResponse);
        return devices;
    }

}
