package com.realtime_monitoring.device_management.imp;

import java.util.UUID;

import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish;
import org.springframework.stereotype.Service;

import com.realtime_monitoring.device_management.entity.CommandStatus;
import com.realtime_monitoring.device_management.entity.DeviceCommand;
import com.realtime_monitoring.device_management.mqtt.MqttCommandPub;
import com.realtime_monitoring.device_management.repository.DeviceCommandRepository;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceCommandeServiceImp implements DeviceCommandeService {

    private final DeviceCommandRepository deviceCommandRepository;
    private final MqttCommandPub mqttPubLisher;

    @Override
    @Transactional
    public DeviceCommand createCommand(UUID deviceId, UUID tenantId, UUID userId, String command) {

        DeviceCommand deviceCommand = new DeviceCommand();

        deviceCommand.setDeviceId(deviceId);
        deviceCommand.setTenantId(tenantId);
        deviceCommand.setUserId(userId);
        deviceCommand.setCommand(command);
        deviceCommand.setStatus(CommandStatus.PENDING);

        DeviceCommand saved = deviceCommandRepository.save(deviceCommand);

        try {
            mqttPubLisher.sendCommand(tenantId, deviceId, saved.getId(), command);
            saved.setStatus(CommandStatus.SENT);

        } catch (Exception e) {
            saved.setStatus(CommandStatus.FAILED);

            System.err.println(e.getMessage());
        }

        return deviceCommandRepository.save(saved);
    }
}