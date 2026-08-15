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
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import com.influxdb.client.InfluxDBClient;

import javax.net.ssl.SSLContext;

@Configuration
@EnableIntegration
public class MqttConfig {

    @Autowired
    private InfluxDBClient influxDBClient;

    @Value("${mqtt.broker}")
    private String broker;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    @Value("${mqtt.qos}")
    private int qos;

    private final SSLContext sslContext;

    public MqttConfig(@Qualifier("mqttSslContext") SSLContext sslContext) {
        this.sslContext = sslContext;
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
    public IntegrationFlow mqttMessageFlow(
            @Qualifier("mqttInputChannel") MessageChannel mqttInputChannel) {

        return IntegrationFlow
                .from(mqttInputChannel)
                .handle(message -> {
                    String payload = message.getPayload().toString();
                    String receivedTopic = message.getHeaders()
                            .get("mqtt_receivedTopic", String.class);

                    System.out.println("========================================");
                    System.out.println("MQTT MESSAGE RECEIVED");
                    System.out.println("Topic: " + receivedTopic);
                    System.out.println("Payload: " + payload);
                    System.out.println("========================================");
                })
                .get();
    }
}