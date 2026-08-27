package com.realtime_monitoring.device_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.realtime_monitoring.device_management.dto.CreateDeviecRequest;
import com.realtime_monitoring.device_management.dto.DeviceLog;
import com.realtime_monitoring.device_management.dto.DeviceLogSSEService;
import com.realtime_monitoring.device_management.dto.DeviceResponse;
import com.realtime_monitoring.device_management.dto.ExecuteCommand;
import com.realtime_monitoring.device_management.dto.ProvisionRequest;
import com.realtime_monitoring.device_management.dto.ProvisionResponse;
import com.realtime_monitoring.device_management.dto.UpdateDeviceRequest;
import com.realtime_monitoring.device_management.entity.DeviceCommand;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;
import com.realtime_monitoring.device_management.service.DeviceLogService;
import com.realtime_monitoring.device_management.service.DeviceService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;
    private final DeviceCommandeService deviceCommandeService;
    private final DeviceLogSSEService deviceLogSseService;

    @PostMapping
    public ResponseEntity<DeviceResponse> createDevice(@Valid @RequestBody CreateDeviecRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.CreateDevice(request));
    }

    @GetMapping(value = "/{deviceId}/logs/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs(@PathVariable String deviceId) {
        return deviceLogSseService.subscribe(deviceId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getDevice(@PathVariable UUID id) {
        DeviceResponse deviceResponse = deviceService.getDEviceById(id);
        return ResponseEntity.ok(deviceResponse);
    }

    @GetMapping
    public ResponseEntity<Page<DeviceResponse>> getAllDevices(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(deviceService.getAllDevices(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> updateDevice(@PathVariable UUID id,
            @Valid @RequestBody UpdateDeviceRequest request) {

        return ResponseEntity.ok(deviceService.updateDevice(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/provision/{token}")
    public ProvisionResponse provisionDevice(@PathVariable String token,
            @RequestBody ProvisionRequest provisionRequest) {
        return deviceService.provisionDevice(token, provisionRequest);
    }

    @PostMapping("/{deviceId}/commands")
    public ResponseEntity<DeviceCommand> execute(@PathVariable UUID deviceId, @RequestBody ExecuteCommand command,
            HttpServletRequest request) {

        DeviceCommand deviceCommand = this.deviceCommandeService.createCommand(deviceId,
                UUID.fromString(request.getHeader("X-User-Tenant-Id")), UUID.fromString(request.getHeader("X-User-Id")),
                command.getCommand());
        return ResponseEntity.ok(deviceCommand);
    }

    @GetMapping("/commands/{commandId}")
    public ResponseEntity<DeviceCommand> getCommand(@PathVariable UUID commandId) {
        return ResponseEntity.ok(deviceCommandeService.getCommandById(commandId));
    }

    private final DeviceLogService deviceLogService;

    @GetMapping("/{deviceId}/logs")
    public ResponseEntity<Page<DeviceLog>> getLogsByDevice(@PathVariable String deviceId,
            @RequestParam(required = false) String level,
            @PageableDefault(page = 0, size = 50) Pageable pageable) {

        if (level != null && !level.isBlank()) {
            return ResponseEntity.ok(
                    deviceLogService.getLogsByDeviceAndLevel(deviceId, level.toUpperCase(), pageable));
        }

        return ResponseEntity.ok(deviceLogService.getLogsByDevice(deviceId, pageable));
    }

    @GetMapping("/tenants/{tenantId}/logs")
    public ResponseEntity<Page<DeviceLog>> getLogsByTenant(
            @PathVariable String tenantId,
            @PageableDefault(page = 0, size = 50) Pageable pageable) {

        return ResponseEntity.ok(deviceLogService.getLogsByTenant(tenantId, pageable));
    }

    // @GetMapping("/user/me")
    // public ResponseEntity<List<Notification>>
    // getNotificationsByUserHeader(HttpServletRequest request) {
    // System.out.println("X-User-Id: " + request.getHeader("X-User-Id"));
    // System.out.println("X-User-Email: " + request.getHeader("X-User-Email"));
    // System.out.println("X-User-Role: " + request.getHeader("X-User-Role"));
    // System.out.println("X-User-Tenant-Id: " +
    // request.getHeader("X-User-Tenant-Id"));
    // System.out.println("X-User-Name: " + request.getHeader("X-User-Name"));
    // System.out.println("X-User-Id::::::::::::::: " +
    // request.getHeader("X-User-Id"));
    // return
    // ResponseEntity.ok(notificationRepository.findByUserIdOrderByCreatedAtDesc(request.getHeader("X-User-Id")));
    // }

}