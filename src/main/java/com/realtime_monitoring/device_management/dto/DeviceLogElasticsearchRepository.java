package com.realtime_monitoring.device_management.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DeviceLogElasticsearchRepository
        extends ElasticsearchRepository<DeviceLog, String> {
 
    Page<DeviceLog> findByDeviceIdOrderByDeviceTimestampDesc(String deviceId, Pageable pageable);
 
    Page<DeviceLog> findByTenantIdOrderByDeviceTimestampDesc(String tenantId, Pageable pageable);
 
    Page<DeviceLog> findByDeviceIdAndLevelOrderByDeviceTimestampDesc(
            String deviceId, String level, Pageable pageable);
}