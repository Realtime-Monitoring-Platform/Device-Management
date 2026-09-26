package com.realtime_monitoring.device_management.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.realtime_monitoring.device_management.dto.CommandResult;
import com.realtime_monitoring.device_management.entity.DeviceCommand;

public interface DeviceCommandeService {
    DeviceCommand createCommand(UUID deviceId, UUID tenantId, UUID userId, String command);

    void handleCommandResult(CommandResult result);

    DeviceCommand getCommandById(UUID commandId);

    Page<DeviceCommand> getCommandsByDeviceId(UUID deviceId, Pageable pageable);

    
    DeviceCommand createCommand(
            UUID deviceId,
            UUID tenantId,
            UUID userId,
            String command,
            boolean aiGenerated);

    DeviceCommand createAiCommand(
            UUID deviceId,
            UUID tenantId,
            String command);

}// (UUID, UUID, UUID, String)
