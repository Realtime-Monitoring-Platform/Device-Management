package com.realtime_monitoring.device_management.event;
import java.time.Instant;
import java.util.UUID;

public record DomainEvent(
    UUID eventId,
    String eventType,
    UUID aggregateId,
    String aggregateType,
    Instant occurredAt
) {}
