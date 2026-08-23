package com.realtime_monitoring.device_management.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "device")
@AllArgsConstructor
@NoArgsConstructor

public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    
    private UUID tenantId;

    private UUID teamId;

    private UUID assignedUserId;

    
    private String deviceName;

    private String model;

    private String manufacturer;

    
    private String hostname;

    private String ipAddress;

    private String macAddress;

    @Column(nullable = true, unique = true, updatable = false)
    private String deviceIdentifier;

    private String location;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status=DeviceStatus.OFFLINE;
     
    private String osName;
    private String osVersion;
    private String kernelVersion;
    private Long cpuCount;
    private Long totalMemoryKb;
    private LocalDateTime lastSeen;
   
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}