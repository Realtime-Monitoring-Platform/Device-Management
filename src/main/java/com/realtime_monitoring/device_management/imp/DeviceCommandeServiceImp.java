package com.realtime_monitoring.device_management.imp;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.realtime_monitoring.device_management.entity.CommandStatus;
import com.realtime_monitoring.device_management.entity.DeviceCommand;
import com.realtime_monitoring.device_management.repository.DeviceCommandRepository;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceCommandeServiceImp implements DeviceCommandeService {

    private final DeviceCommandRepository deviceCommandRepository;

    @Override
    public DeviceCommand createCommand(UUID deviceId, UUID tenantId, UUID userId, String command) {
        DeviceCommand deviceCommand = new DeviceCommand();

        deviceCommand.setDeviceId(deviceId);
        deviceCommand.setTenantId(tenantId);
        deviceCommand.setUserId(userId);
        deviceCommand.setCommand(command);
        deviceCommand.setStatus(CommandStatus.PENDING);

        return deviceCommandRepository.save(deviceCommand);
    }

}