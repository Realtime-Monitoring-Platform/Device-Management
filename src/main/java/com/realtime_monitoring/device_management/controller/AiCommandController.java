package com.realtime_monitoring.device_management.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.realtime_monitoring.device_management.dto.AiCommandRequest;
import com.realtime_monitoring.device_management.entity.DeviceCommand;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ai/devices")
@RequiredArgsConstructor
public class AiCommandController {

    private final DeviceCommandeService deviceCommandeService;

    @PostMapping("/{deviceId}/commands")
    public ResponseEntity<DeviceCommand> executeAiCommand(
            @PathVariable UUID deviceId,
            @RequestBody AiCommandRequest request) {

                
        DeviceCommand deviceCommand =
                deviceCommandeService.createAiCommand(
                       // null,
                        deviceId,
                        request.getTenantId(),
                        request.getCommand()
                );

        return ResponseEntity.ok(deviceCommand);
    }
}