package com.realtime_monitoring.device_management.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realtime_monitoring.device_management.entity.DeviceToken;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {
    
}
