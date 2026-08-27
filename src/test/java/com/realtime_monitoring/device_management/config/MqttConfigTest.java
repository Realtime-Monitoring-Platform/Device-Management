package com.realtime_monitoring.device_management.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

import javax.net.ssl.SSLContext;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "mqtt.broker=ssl://192.168.1.122:8883",
    "mqtt.client-id=test-client",
    "mqtt.topic=devices/+/metrics",
    "mqtt.qos=1"
})
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
