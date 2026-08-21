package com.realtime_monitoring.device_management.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MqttConfigTest {

    @Autowired
    private MessageChannel mqttInputChannel;

    @Autowired
    private IntegrationFlow mqttMessageFlow;

    @Test
    void contextLoads() {
        // Verify that the MQTT configuration loads successfully
        assertNotNull(mqttInputChannel, "mqttInputChannel should not be null");
        assertNotNull(mqttMessageFlow, "mqttMessageFlow should not be null");
        assertTrue(mqttInputChannel instanceof DirectChannel, "Channel should be a DirectChannel");
    }
}
