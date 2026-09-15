package com.realtime_monitoring.device_management.config;

import com.realtime_monitoring.device_management.dto.DeviceLogSSEService;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtime_monitoring.device_management.dto.CommandResult;
import com.realtime_monitoring.device_management.dto.DeviceLog;
import com.realtime_monitoring.device_management.dto.DevicemEtrics;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;
import com.realtime_monitoring.device_management.service.DeviceLogMessage;
import com.realtime_monitoring.device_management.service.DeviceLogService;
import com.realtime_monitoring.device_management.service.DeviceService;
import com.realtime_monitoring.device_management.service.IncidentDetectorService;
import com.realtime_monitoring.device_management.service.MetricsService;

@Configuration
@EnableIntegration
public class MqttConfig {

    private final DeviceLogSSEService deviceLogSSEService;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    @Value("${mqtt.qos}")
    private int qos;

    @Value("${mqtt.command-results-topic}")
    private String commandResultsTopic;

    @Value("${mqtt.logs-topic}")
    private String logsTopic;
    private final IncidentDetectorService incidentDetectorService;
    private final ObjectMapper objectMapper;
    private final MetricsService metricService;
    private final DeviceCommandeService deviceCommandeService;
    private final DeviceLogService deviceLogService;

    public MqttConfig(ObjectMapper objectMapper,
            MetricsService metricService,
            DeviceCommandeService deviceCommandeService,
            DeviceLogService deviceLogService, DeviceLogSSEService deviceLogSSEService,
            IncidentDetectorService incidentDetectorService) {
        this.objectMapper = objectMapper;
        this.incidentDetectorService = incidentDetectorService;
        this.metricService = metricService;
        this.deviceCommandeService = deviceCommandeService;
        this.deviceLogService = deviceLogService;
        this.deviceLogSSEService = deviceLogSSEService;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttCommandResultsChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttLogsChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInbound(
            @Qualifier("mqttInputChannel") MessageChannel mqttInputChannel,
            DefaultMqttPahoClientFactory mqttClientFactory) {

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId,
                mqttClientFactory, topic);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttInputChannel);

        return adapter;
    }

    @Bean
    public IntegrationFlow mqttMessageFlow(
            @Qualifier("mqttInputChannel") MessageChannel mqttInputChannel) {

        return IntegrationFlow
                .from(mqttInputChannel)
                .handle(message -> {

                    try {
                        String payload = message.getPayload().toString();
                        String receivedTopic = message.getHeaders()
                                .get("mqtt_receivedTopic", String.class);

                        // System.out.println("========================================");
                        // System.out.println("MQTT MESSAGE RECEIVED");
                        // System.out.println("Topic: " + receivedTopic);
                        // System.out.println("Payload: " + payload);
                        // System.out.println("========================================");

                        DevicemEtrics metric = objectMapper.readValue(payload, DevicemEtrics.class);

                        // System.out.println("Device ID: " + metric.getDevice_id());
                        // System.out.println("Tenant ID: " + metric.getTenant_id());
                        // System.out.println("CPU: " + metric.getCpu());
                        // System.out.println("RAM: " + metric.getRam());

                        metricService.save(metric);

                        System.out.println("Metric saved successfully to InfluxDB");

                    } catch (Exception e) {
                        System.err.println("Failed to process MQTT metric");
                        e.printStackTrace();
                    }
                })
                .get();
    }

    @Bean
    public IntegrationFlow mqttLogsFlow(
            @Qualifier("mqttLogsChannel") MessageChannel mqttLogsChannel) {

        return IntegrationFlow
                .from(mqttLogsChannel)
                .handle(message -> {

                    try {

                        String payload = message.getPayload().toString();

                        String receivedTopic = message.getHeaders()
                                .get("mqtt_receivedTopic", String.class);

                        System.out.println("========================================");
                        System.out.println("MQTT LOGS RECEIVED");
                        System.out.println("Topic: " + receivedTopic);
                        System.out.println("Payload: " + payload);
                        System.out.println("========================================");

                        JsonNode root = objectMapper.readTree(payload);

                        /*
                         * ========================================
                         * CASE 1: SINGLE LOG
                         * ========================================
                         */
                        if (root.has("message") && !root.has("logs")) {

                            DeviceLogMessage logMessage = objectMapper.treeToValue(
                                    root,
                                    DeviceLogMessage.class);

                            processDeviceLog(logMessage);

                            return;
                        }

                        /*
                         * ========================================
                         * CASE 2: BATCH OF LOGS
                         * ========================================
                         */
                        if (root.has("logs")) {

                            DeviceLogsMessage logsMessage = objectMapper.treeToValue(
                                    root,
                                    DeviceLogsMessage.class);

                            System.out.println(
                                    "Device ID: "
                                            + logsMessage.getDeviceId());

                            System.out.println(
                                    "Tenant ID: "
                                            + logsMessage.getTenantId());

                            if (logsMessage.getLogs() == null ||
                                    logsMessage.getLogs().isEmpty()) {

                                System.out.println(
                                        "No logs received for device: "
                                                + logsMessage.getDeviceId());

                                return;
                            }

                            System.out.println(
                                    "Number of logs: "
                                            + logsMessage.getLogs().size());

                            for (DeviceLogMessage logMessage : logsMessage.getLogs()) {

                                processDeviceLog(logMessage);
                            }

                            return;
                        }

                        System.out.println(
                                "Unknown MQTT log payload format");

                    } catch (Exception e) {

                        System.err.println(
                                "Failed to process MQTT log");

                        e.printStackTrace();
                    }
                })
                .get();
    }

    private void processDeviceLog(DeviceLogMessage logMessage) {

        System.out.println("Processing log for device: " + logMessage.getDeviceId());
        System.out.println("Tenant: " + logMessage.getTenantId());
        System.out.println("Level: " + logMessage.getLevel());
        System.out.println("Message: " + logMessage.getMessage());
        deviceLogService.saveLog(logMessage);
        DeviceLog log = new DeviceLog();

        log.setDeviceId(logMessage.getDeviceId());
        log.setTenantId(logMessage.getTenantId());
        log.setLevel(logMessage.getLevel());
        log.setMessage(logMessage.getMessage());

        log.setDeviceTimestamp(Instant.ofEpochMilli(logMessage.getTimestamp()));

        log.setReceivedAt(Instant.now());

        incidentDetectorService.checkLogIncident(log);

        deviceLogSSEService.publish(
                logMessage.getDeviceId(),
                logMessage);

        System.out.println("device log saved successfully.");
    }

    @Bean
    public MessageProducer mqttCommandResultsInbound(
            @Qualifier("mqttCommandResultsChannel") MessageChannel mqttCommandResultsChannel,
            DefaultMqttPahoClientFactory mqttClientFactory) {

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "-results",
                mqttClientFactory,
                commandResultsTopic);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttCommandResultsChannel);

        return adapter;
    }

    @Bean
    public MessageProducer mqttLogsInbound(
            @Qualifier("mqttLogsChannel") MessageChannel mqttLogsChannel,
            DefaultMqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "-logs", mqttClientFactory, logsTopic);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttLogsChannel);

        return adapter;
    }

    @Bean
    public IntegrationFlow mqttCommandResultsFlow(
            @Qualifier("mqttCommandResultsChannel") MessageChannel mqttCommandResultsChannel) {

        return IntegrationFlow
                .from(mqttCommandResultsChannel)
                .handle(message -> {

                    try {
                        String payload = message.getPayload().toString();
                        String receivedTopic = message.getHeaders()
                                .get("mqtt_receivedTopic", String.class);

                        // System.out.println("========================================");
                        // System.out.println("MQTT COMMAND RESULT RECEIVED");
                        // System.out.println("Topic: " + receivedTopic);
                        // System.out.println("Payload: " + payload);
                        // System.out.println("========================================");

                        CommandResult result = objectMapper.readValue(payload, CommandResult.class);

                        deviceCommandeService.handleCommandResult(result);

                        System.out.println("Command result saved successfully.");

                    } catch (Exception e) {
                        System.err.println("Failed to process MQTT command result");
                        e.printStackTrace();
                    }
                })
                .get();
    }
}