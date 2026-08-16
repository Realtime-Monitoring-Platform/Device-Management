package com.realtime_monitoring.device_management.mqtt;

import java.util.UUID;

import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.realtime_monitoring.device_management.dto.DeviceCommandMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MqttCommandPub {
    private final MqttPahoMessageHandler mqttPahoMessageHandler;

    public void sendCommand(UUID tenantId, UUID deviceId, UUID commandId, String command) {
        String topic = "tenants/" + tenantId + "/devices/" + deviceId + "/commands";

        DeviceCommandMessage message = new DeviceCommandMessage();
        message.setCommandId(commandId);
        message.setDeviceId(deviceId);
        message.setCommand(command);

        mqttPahoMessageHandler.handleMessage(MessageBuilder.withPayload(message).setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1).build());

    }
}
