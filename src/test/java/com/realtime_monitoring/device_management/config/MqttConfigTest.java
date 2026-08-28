package com.realtime_monitoring.device_management.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtime_monitoring.device_management.dto.DeviceLogSSEService;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;
import com.realtime_monitoring.device_management.service.DeviceLogService;
import com.realtime_monitoring.device_management.service.MetricsService;

/**
 * Verifies that {@link MqttConfig} wires up its channels, integration flows and inbound MQTT
 * adapters correctly.
 *
 * <p>This is deliberately a pure unit test (no Spring application context) so that it runs in CI /
 * SonarCloud without requiring any external infrastructure such as a PostgreSQL database, the private
 * MQTT broker ({@codemvn -B verify sonar:sonar -DskipTests -Dsonar.organization="realtime-monitoring-platform" -Dsonar.projectKey="Realtime-Monitoring-Platform_Device-Management" -Dsonar.projectName="Device-Management" -Dsonar.host.url="https://sonarcloud.io" -Dsonar.token="4fc7c0f79734080a313b94f60a5064a199175795" ssl://192.168.1.122:8883}) or the MQTT TLS/SSL certificate files. The original
 * {@code @SpringBootTest} variant loaded the full application context and failed on the CI runner
 * with {@code Connection to localhost:5432 refused} because no database is available there.</p>
 */
class MqttConfigTest {

    private MqttConfig buildMqttConfig() {
        MqttConfig config = new MqttConfig(
                new ObjectMapper(),
                mock(MetricsService.class),
                mock(DeviceCommandeService.class),
                mock(DeviceLogService.class),
                mock(DeviceLogSSEService.class));

        // Inject the @Value fields that Spring would normally resolve from the active properties.
        ReflectionTestUtils.setField(config, "clientId", "test-client");
        ReflectionTestUtils.setField(config, "topic", "devices/+/metrics");
        ReflectionTestUtils.setField(config, "qos", 1);
        ReflectionTestUtils.setField(config, "commandResultsTopic", "devices/+/command-results");
        ReflectionTestUtils.setField(config, "logsTopic", "devices/+/logs");

        return config;
    }

    @Test
    void channelsAreConfigured() {
        MqttConfig config = buildMqttConfig();

        assertTrue(config.mqttInputChannel() instanceof DirectChannel,
                "mqttInputChannel should be a DirectChannel");
        assertTrue(config.mqttCommandResultsChannel() instanceof DirectChannel,
                "mqttCommandResultsChannel should be a DirectChannel");
        assertTrue(config.mqttLogsChannel() instanceof DirectChannel,
                "mqttLogsChannel should be a DirectChannel");
    }

    @Test
    void messageFlowsAreConfigured() {
        MqttConfig config = buildMqttConfig();

        MessageChannel inputChannel = config.mqttInputChannel();
        assertNotNull(inputChannel, "mqttInputChannel should not be null");
        assertNotNull(config.mqttMessageFlow(inputChannel), "mqttMessageFlow should not be null");

        assertNotNull(config.mqttLogsFlow(config.mqttLogsChannel()), "mqttLogsFlow should not be null");
        assertNotNull(config.mqttCommandResultsFlow(config.mqttCommandResultsChannel()),
                "mqttCommandResultsFlow should not be null");
    }

    @Test
    void inboundAdaptersAreConfigured() {
        MqttConfig config = buildMqttConfig();
        // The adapters only open a broker connection when started; merely creating them performs no
        // I/O, so this can safely run without a reachable MQTT broker.
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();

        assertNotNull(config.mqttInbound(config.mqttInputChannel(), clientFactory),
                "mqttInbound should not be null");
        assertNotNull(config.mqttCommandResultsInbound(config.mqttCommandResultsChannel(), clientFactory),
                "mqttCommandResultsInbound should not be null");
        assertNotNull(config.mqttLogsInbound(config.mqttLogsChannel(), clientFactory),
                "mqttLogsInbound should not be null");
    }
}
