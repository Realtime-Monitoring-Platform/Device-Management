package com.realtime_monitoring.device_management.dto;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "device-logs",createIndex = false)
public class DeviceLog {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String deviceId;

    @Field(type = FieldType.Keyword)
    private String tenantId;

    @Field(type = FieldType.Keyword)
    private String level;

    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Date)
    private Instant deviceTimestamp;

    @Field(type = FieldType.Date)
    private Instant receivedAt;

}