package com.realtime_monitoring.device_management.event;
import java.util.UUID;

public record DeviceDeletedEvent(
    DomainEvent event,
        UUID id
) {}
