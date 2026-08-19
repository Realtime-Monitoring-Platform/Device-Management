package com.realtime_monitoring.device_management.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;

@Entity
@Getter
@Setter
@Table
@RequiredArgsConstructor
public class DeviceCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID deviceId;

    private UUID tenantId;

    @Enumerated(EnumType.STRING)
    private CommandStatus status;

    private UUID userId;
    @Column(columnDefinition = "TEXT")
    private String stdout;
    
    private String command;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
      
    private int exitCode;

}
