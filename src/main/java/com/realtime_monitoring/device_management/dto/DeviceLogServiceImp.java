package com.realtime_monitoring.device_management.dto;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.realtime_monitoring.device_management.service.DeviceLogMessage;
import com.realtime_monitoring.device_management.service.DeviceLogService;
import com.realtime_monitoring.device_management.service.IncidentDetectorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceLogServiceImp implements DeviceLogService {

    private final DeviceLogElasticsearchRepository deviceLogRepository;
    //private final IncidentDetectorService incidentDetectorService;
    @Override
    public void saveLog(DeviceLogMessage message) {

        DeviceLog doc = new DeviceLog();

        doc.setDeviceId(message.getDeviceId());
        doc.setTenantId(message.getTenantId());
        doc.setLevel(message.getLevel());
        doc.setMessage(message.getMessage());
        doc.setDeviceTimestamp(Instant.ofEpochMilli(message.getTimestamp()));
        doc.setReceivedAt(Instant.now());

        DeviceLog savedLog = deviceLogRepository.save(doc);
       // incidentDetectorService.checkLogIncident(savedLog);
        // System.out.println("========================================");
        // System.out.println("DEVICE LOG SAVED");
        // System.out.println("Device ID: " + savedLog.getDeviceId());
        // System.out.println("Tenant ID: " + savedLog.getTenantId());
        // System.out.println("Level: " + savedLog.getLevel());
        // System.out.println("Message: " + savedLog.getMessage());
        // System.out.println("========================================");

    }

    @Override
    public Page<DeviceLog> getLogsByDevice(String deviceId, Pageable pageable) {
        return deviceLogRepository.findByDeviceIdOrderByDeviceTimestampDesc(deviceId, pageable);
    }

    @Override
    public Page<DeviceLog> getLogsByTenant(String tenantId, Pageable pageable) {
        return deviceLogRepository.findByTenantIdOrderByDeviceTimestampDesc(tenantId, pageable);
    }

    @Override
    public Page<DeviceLog> getLogsByDeviceAndLevel(String deviceId, String level, Pageable pageable) {
        return deviceLogRepository.findByDeviceIdAndLevelOrderByDeviceTimestampDesc(deviceId, level, pageable);
    }

    @Override
    public List<DeviceLog> getRecentLogs(String deviceId, int minutes) {
        Instant to = Instant.now();
        Instant from = to.minus(minutes, ChronoUnit.MINUTES);
        return deviceLogRepository.findByDeviceIdAndDeviceTimestampBetweenOrderByDeviceTimestampDesc(deviceId, from,
                to);
    }
}