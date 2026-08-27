package com.realtime_monitoring.device_management.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.realtime_monitoring.device_management.entity.Device;
import com.realtime_monitoring.device_management.kafka.event.DeviceCreatedEvent;
import com.realtime_monitoring.device_management.kafka.event.DeviceDeletedEvent;
import com.realtime_monitoring.device_management.kafka.event.DeviceUpdatedEvent;
import com.realtime_monitoring.device_management.kafka.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Component
public class DeviceProducer {
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public void sendDeviceCreation(Device device) {
                DomainEvent event = new DomainEvent(
                                UUID.randomUUID(),
                                "DEVICE_CREATED",
                                device.getId(),
                                "DEVICE",
                                Instant.now());

                DeviceCreatedEvent deviceEvent = new DeviceCreatedEvent(
                                device.getId(),
                                event,
                                device.getAssignedUserId(),
                                device.getDeviceName(),
                                device.getModel(),
                                device.getManufacturer(),
                                device.getHostname(),
                                device.getIpAddress(),
                                device.getMacAddress(),
                                device.getLocation(),
                                device.getStatus(),
                                device.getLastSeen(),

                                device.getTenantId(),
                                device.getTeamId(),
                                device.getOsName(),
                                device.getOsVersion(),
                                device.getKernelVersion(),
                                device.getCpuCount(),
                                device.getTotalMemoryKb()
                                
                         //       device.getMqttHashPassword(),
                           //     device.getMqttPassword(),
                             //   device.getMqttUsername()

                );

                log.info("sending device creation event:::::::::::::::::::::::::::::: {}", deviceEvent);
                kafkaTemplate.send("device-events-v6", device.getId().toString(), deviceEvent);
        }

        public void sendDeviceUpdate(Device device) {
                DomainEvent event = new DomainEvent(
                                UUID.randomUUID(),
                                "DEVICE_UPDATED",
                                device.getId(),
                                "DEVICE",
                                Instant.now());

                DeviceUpdatedEvent deviceEvent = new DeviceUpdatedEvent(
                                device.getId(),
                                event,
                                device.getAssignedUserId(),
                                device.getDeviceName(),
                                device.getModel(),
                                device.getManufacturer(),
                                device.getHostname(),
                                device.getIpAddress(),
                                device.getMacAddress(),
                                device.getLocation(),
                                device.getStatus(),
                                device.getLastSeen(),
                                device.getTenantId(),
                                device.getTeamId(),
                                device.getOsName(),
                                device.getOsVersion(),
                                device.getKernelVersion(),
                                device.getCpuCount(),
                                device.getTotalMemoryKb()

                        //        device.getMqttHashPassword(),
                          //      device.getMqttPassword(),
                            //    device.getMqttUsername()
                        );

                log.info("sending device update event: {}", deviceEvent);
                kafkaTemplate.send("device-events-v6", device.getId().toString(), deviceEvent);
        }

        public void sendDeviceDeleted(UUID deviceId) {

                DomainEvent event = new DomainEvent(
                                UUID.randomUUID(),
                                "TENANT_DELETED",
                                deviceId,
                                "TENANT",
                                Instant.now());

                DeviceDeletedEvent deviceEvent = new DeviceDeletedEvent(
                                event,
                                deviceId);

                log.info("Sending device deletion event: {}", deviceEvent);

                kafkaTemplate.send(
                                "device-events-v6",
                                deviceId.toString(),
                                deviceEvent);
        }
}
