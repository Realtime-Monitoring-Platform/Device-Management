package com.realtime_monitoring.device_management.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class DeviceCommandMessage {
   private UUID commandId;
   private UUID deviceId;
   private String command;
   
}
