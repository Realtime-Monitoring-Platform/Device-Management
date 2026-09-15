package com.realtime_monitoring.device_management.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record CommandRequestEvent(
    UUID incidentId,
    UUID deviceId,
    UUID tenantId,
    
    UUID requestedBy,
    String command,
    String source,
    Instant requestedAt
) {}