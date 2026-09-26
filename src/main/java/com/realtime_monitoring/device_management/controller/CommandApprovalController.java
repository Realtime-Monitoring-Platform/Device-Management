package com.realtime_monitoring.device_management.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realtime_monitoring.device_management.dto.CommandApprovalRequest;
import com.realtime_monitoring.device_management.entity.DeviceCommand;
import com.realtime_monitoring.device_management.service.CommandSafetyValidator;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ai/commands")
@RequiredArgsConstructor
public class CommandApprovalController {

    private final DeviceCommandeService deviceCommandeService;
    private final CommandSafetyValidator commandSafetyValidator;

    @PostMapping("/approve")
    public ResponseEntity<?> approve(@RequestBody CommandApprovalRequest request) {
        // if (!commandSafetyValidator.isAllowed(request.getCommand())) {
        //     return ResponseEntity.ok(Map.of(
        //         "status", "REJECTED",
        //         "reason", "Command is not allowed"));
        // }

        
        DeviceCommand deviceCommand = deviceCommandeService.createAiCommand(
            //request.getIncidentId(),
                request.getDeviceId(),
                request.getTenantId(),
                request.getCommand());

        return ResponseEntity.ok(Map.of(
                "status", "APPROVED",
                "command", request.getCommand(),
                "commandId", deviceCommand.getId()));
    }
}