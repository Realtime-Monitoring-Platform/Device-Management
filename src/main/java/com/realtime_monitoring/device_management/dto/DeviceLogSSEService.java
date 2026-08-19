package com.realtime_monitoring.device_management.dto;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.realtime_monitoring.device_management.service.DeviceLogMessage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DeviceLogSSEService {
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String deviceId) {
        SseEmitter emitter = new SseEmitter(0L);

        emitters.computeIfAbsent(deviceId, key -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(deviceId, emitter));
        emitter.onCompletion(() -> remove(deviceId, emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("Connected to device " + deviceId));
        } catch (IOException e) {
            remove(deviceId, emitter);
        }

        return emitter;
    }

    public void publish(String deviceId, DeviceLogMessage log) {
        List<SseEmitter> deviceEmitters = emitters.get(deviceId);

        if (deviceEmitters == null) {
            return;
        }
        for (SseEmitter emitter : deviceEmitters) {
            try {
                System.out.println("========================================");
                System.out.println("PUBLISHING LOG TO SSE");
                System.out.println("Device ID: " + log.getDeviceId());
                System.out.println("Tenant ID: " + log.getTenantId());
                System.out.println("Level: " + log.getLevel());
                System.out.println("Service: " + log.getService());
                emitter.send(SseEmitter.event().name("device-log").data(log));
            } catch (IOException e) {
                remove(deviceId, emitter);
            }
        }
    }

    private void remove(String deviceId, SseEmitter emitter) {

        List<SseEmitter> deviceEmitters = emitters.get(deviceId);

        if (deviceEmitters != null) {
            deviceEmitters.remove(emitter);
            if (deviceEmitters.isEmpty()) {
                emitters.remove(deviceId);
            }
        }
    }

}
