package com.realtime_monitoring.device_management.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.realtime_monitoring.device_management.dto.DevicemEtrics;
import com.realtime_monitoring.device_management.service.MetricsService;

import javax.net.ssl.SSLContext;

@Configuration
@EnableIntegration
public class MqttConfig {

    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    @Value("${mqtt.qos}")
    private int qos;

    private final ObjectMapper objectMapper;
    private final SSLContext sslContext;
    private final MetricsService metricService;

    public MqttConfig(@Qualifier("mqttSslContext") SSLContext sslContext,
            ObjectMapper objectMapper,
            MetricsService MetricService) {
        this.sslContext = sslContext;
        this.objectMapper = objectMapper;
        this.metricService = MetricService;
    }

    @Bean
    public DefaultMqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { broker });
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(30);
        options.setConnectionTimeout(10);
        options.setSocketFactory(sslContext.getSocketFactory());

        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
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
        adapter.setOutputChannel(mqttInputChannel); // required

        return adapter;
    }

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

    @Bean
    public IntegrationFlow mqttMessageFlow(
            @Qualifier("mqttInputChannel") MessageChannel mqttInputChannel) {
        // String payload = mqttInputChannel..getPayload().toString();
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

                        // JSON -> Java object
                        DevicemEtrics metric = objectMapper.readValue(payload, DevicemEtrics.class);

                        System.out.println("Device ID: " + metric.getDevice_id());
                        System.out.println("Tenant ID: " + metric.getTenant_id());
                        System.out.println("CPU: " + metric.getCpu());
                        System.out.println("RAM: " + metric.getRam());

                        // Save to InfluxDB
                        metricService.save(metric);

                        System.out.println("Metric saved successfully to InfluxDB");

                    } catch (Exception e) {

                        System.err.println("Failed to process MQTT metric");

                        e.printStackTrace();
                    }
                })
                .get();
    }
}