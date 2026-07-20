package com.realtime_monitoring.device_management.exceptions;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String message){
        super(message);
    }
}
