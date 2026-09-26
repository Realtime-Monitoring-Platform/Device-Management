package com.realtime_monitoring.device_management.kafka;

import com.realtime_monitoring.device_management.dto.CommandResult;
import com.realtime_monitoring.device_management.entity.DeviceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommandResultProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void publish(DeviceCommand command, CommandResult result) {
        Map<String, Object> event = new HashMap<>();
        event.put("commandId", command.getId());
        event.put("incidentId", command.getIncidentId());
        event.put("deviceId", command.getDeviceId());
        event.put("tenantId", command.getTenantId());
        event.put("command", command.getCommand());
        event.put("status", result.getStatus());
        event.put("exitCode", result.getExitCode());
        event.put("stdout", result.getStdout());
        event.put("error", null);

        kafkaTemplate.send("command-results", command.getId().toString(), event);
    }
}