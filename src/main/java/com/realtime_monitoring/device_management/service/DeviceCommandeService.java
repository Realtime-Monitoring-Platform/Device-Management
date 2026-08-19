package com.realtime_monitoring.device_management.service;

import java.util.UUID;

import com.realtime_monitoring.device_management.dto.CommandResult;
import com.realtime_monitoring.device_management.entity.DeviceCommand;

public interface DeviceCommandeService {
    DeviceCommand createCommand(UUID deviceId, UUID tenantId, UUID userId, String command);
    void handleCommandResult(CommandResult result);
    DeviceCommand getCommandById(UUID commandId);

}
