package com.realtime_monitoring.device_management.service;

import org.springframework.stereotype.Service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;
import com.realtime_monitoring.device_management.dto.DevicemEtrics;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final InfluxDBClient influxDbClient;

    public void save(DevicemEtrics metrics) {
        Point point = Point.measurement("device_metrics")
                .addTag("device_id", metrics.getDevice_id().toString())
                .addTag("tenant_id", metrics.getTenant_id().toString())
                .addField("cpu", metrics.getCpu())
                .addField("ram", metrics.getRam());
        WriteApiBlocking writeapi=influxDbClient.getWriteApiBlocking();
        writeapi.writePoint(point);
    }
}
