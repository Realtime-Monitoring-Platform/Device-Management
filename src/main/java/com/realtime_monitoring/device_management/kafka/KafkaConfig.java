package com.realtime_monitoring.device_management.kafka;

import com.realtime_monitoring.device_management.kafka.event.CommandRequestEvent;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);

        config.put(
                JsonSerializer.TYPE_MAPPINGS,
                "deviceCreated:com.realtime_monitoring.device_management.kafka.event.DeviceCreatedEvent," +
                        "deviceUpdated:com.realtime_monitoring.device_management.kafka.event.DeviceUpdatedEvent," +
                        "deviceDeleted:com.realtime_monitoring.device_management.kafka.event.DeviceDeletedEvent," +
                        "incidentDetected:com.realtime_monitoring.device_management.kafka.event.IncidentDetectedEvent,"
                        +
                        "commandRequest:com.realtime_monitoring.device_management.kafka.event.CommandRequestEvent");

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {

        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, CommandRequestEvent> commandRequestConsumerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "device-service-command-group");

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);

        JsonDeserializer<CommandRequestEvent> deserializer = new JsonDeserializer<>(CommandRequestEvent.class);

        // FastAPI sends normal JSON without Spring type headers
        deserializer.setUseTypeHeaders(false);

        deserializer.addTrustedPackages(
                "com.realtime_monitoring.device_management.kafka.event");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CommandRequestEvent> commandRequestKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, CommandRequestEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                commandRequestConsumerFactory());

        return factory;
    }
}