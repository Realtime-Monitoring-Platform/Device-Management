package com.realtime_monitoring.device_management.kafka;

import com.realtime_monitoring.device_management.kafka.event.CommandRequestEvent;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandRequestConsumer {

    
    private final DeviceCommandeService deviceCommandeService;

    @KafkaListener(topics = "command-requests", groupId = "device-service-command-group", containerFactory = "commandRequestKafkaListenerContainerFactory")
    public void consume(CommandRequestEvent request) {

        log.info("Received AI command: deviceId={}, command={}",
                request.deviceId(),
                request.command());

        deviceCommandeService.createCommand(
                request.deviceId(),
                request.tenantId(),
                request.requestedBy(),
                request.command());
    }
}