package com.realtime_monitoring.device_management.config;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtime_monitoring.device_management.dto.CommandResult;
import com.realtime_monitoring.device_management.dto.DeviceLog;
import com.realtime_monitoring.device_management.dto.DevicemEtrics;
import com.realtime_monitoring.device_management.service.DeviceCommandeService;
import com.realtime_monitoring.device_management.service.DeviceLogMessage;
import com.realtime_monitoring.device_management.service.DeviceLogService;
import com.realtime_monitoring.device_management.service.DeviceService;
import com.realtime_monitoring.device_management.service.MetricsService;

@Configuration
@EnableIntegration
public class MqttConfig {

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

    private final ObjectMapper objectMapper;
    private final MetricsService metricService;
    private final DeviceCommandeService deviceCommandeService;
    private final DeviceLogService deviceLogService;

    public MqttConfig(ObjectMapper objectMapper,
            MetricsService metricService,
            DeviceCommandeService deviceCommandeService,
            DeviceLogService deviceLogService) {
        this.objectMapper = objectMapper;
        this.metricService = metricService;
        this.deviceCommandeService = deviceCommandeService;
        this.deviceLogService = deviceLogService;
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

                        System.out.println("========================================");
                        System.out.println("MQTT MESSAGE RECEIVED");
                        System.out.println("Topic: " + receivedTopic);
                        System.out.println("Payload: " + payload);
                        System.out.println("========================================");

                        DevicemEtrics metric = objectMapper.readValue(payload, DevicemEtrics.class);

                        System.out.println("Device ID: " + metric.getDevice_id());
                        System.out.println("Tenant ID: " + metric.getTenant_id());
                        System.out.println("CPU: " + metric.getCpu());
                        System.out.println("RAM: " + metric.getRam());

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

                        DeviceLogsMessage logsMessage = objectMapper.readValue(
                                payload,
                                DeviceLogsMessage.class);

                        System.out.println("Device ID: " + logsMessage.getDeviceId());
                        System.out.println("Tenant ID: " + logsMessage.getTenantId());
                        System.out.println("Number of logs: "
                                + logsMessage.getLogs().size());

                        for (DeviceLogMessage logMessage : logsMessage.getLogs()) {

                            System.out.println("----------------------------------------");
                            System.out.println("Device ID: " + logMessage.getDeviceId());
                            System.out.println("Tenant ID: " + logMessage.getTenantId());
                            System.out.println("Level: " + logMessage.getLevel());
                            System.out.println("Service: " + logMessage.getService());
                            System.out.println("Source: " + logMessage.getSource());
                            System.out.println("Message: " + logMessage.getMessage());
                            System.out.println("Timestamp: " + logMessage.getTimestamp());

                            deviceLogService.saveLog(logMessage);
                        }

                        System.out.println("All device logs saved successfully.");

                    } catch (Exception e) {
                        System.err.println("Failed to process MQTT log");
                        e.printStackTrace();
                    }
                })
                .get();
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

                        System.out.println("========================================");
                        System.out.println("MQTT COMMAND RESULT RECEIVED");
                        System.out.println("Topic: " + receivedTopic);
                        System.out.println("Payload: " + payload);
                        System.out.println("========================================");

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