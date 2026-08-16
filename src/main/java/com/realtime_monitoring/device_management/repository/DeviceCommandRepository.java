package com.realtime_monitoring.device_management.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realtime_monitoring.device_management.entity.DeviceCommand;
public interface DeviceCommandRepository extends  JpaRepository<DeviceCommand, UUID> {
    
}
