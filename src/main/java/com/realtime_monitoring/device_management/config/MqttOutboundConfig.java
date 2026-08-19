package com.realtime_monitoring.device_management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

@Configuration
public class MqttOutboundConfig {

    @Value("${mqtt.client-id}")
    private String clientId;

    @Bean
    public MqttPahoMessageHandler mqttOutbound(
            DefaultMqttPahoClientFactory mqttClientFactory) {

        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(
                clientId + "-publisher",
                mqttClientFactory);
        handler.setAsync(true);
        handler.setDefaultQos(1);

        return handler;
    }
}