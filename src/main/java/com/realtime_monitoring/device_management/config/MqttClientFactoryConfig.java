package com.realtime_monitoring.device_management.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;

import javax.net.ssl.SSLContext;

@Configuration
public class MqttClientFactoryConfig {

    @Value("${mqtt.broker}")
    private String broker;

    private final SSLContext sslContext;

    public MqttClientFactoryConfig(@Qualifier("mqttSslContext") SSLContext sslContext) {
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
        if (broker != null && broker.toLowerCase().startsWith("ssl://")) {
            options.setSocketFactory(sslContext.getSocketFactory());
        }

        factory.setConnectionOptions(options);
        return factory;
    }
}