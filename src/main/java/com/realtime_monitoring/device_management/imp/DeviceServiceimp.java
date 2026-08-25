package com.realtime_monitoring.device_management.imp;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import org.apache.kafka.common.config.types.Password;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.realtime_monitoring.device_management.dto.CreateDeviecRequest;
import com.realtime_monitoring.device_management.dto.DeviceInfo;
import com.realtime_monitoring.device_management.dto.DeviceResponse;
import com.realtime_monitoring.device_management.dto.ProvisionRequest;
import com.realtime_monitoring.device_management.dto.ProvisionResponse;
import com.realtime_monitoring.device_management.dto.UpdateDeviceRequest;
import com.realtime_monitoring.device_management.entity.Device;
import com.realtime_monitoring.device_management.entity.DeviceToken;
import com.realtime_monitoring.device_management.exceptions.DeviceNotFoundException;
import com.realtime_monitoring.device_management.kafka.DeviceProducer;
import com.realtime_monitoring.device_management.mapper.DeviceMapper;
import com.realtime_monitoring.device_management.repository.DeviceRepository;
import com.realtime_monitoring.device_management.repository.DeviceTokenRepository;
import com.realtime_monitoring.device_management.security.CertificateService;
import com.realtime_monitoring.device_management.service.DeviceService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class DeviceServiceimp implements DeviceService {

        private final DeviceRepository deviceRepository;
        private final DeviceMapper deviceMapper;
        private final DeviceProducer deviceProducer;
        private final DeviceTokenRepository deviceTokenRepo;
        private final CertificateService certificateService;

        // private final CertificateService certificateService;
        @Override
        public DeviceResponse CreateDevice(CreateDeviecRequest createDeviceRequest) {
                Device device = deviceMapper.toEntity(createDeviceRequest);
                String generatedPassword = Integer.toHexString((int) (Math.random() * Integer.MAX_VALUE));
                
                // String GeneratedId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
                String generatedIdentifier = UUID.randomUUID().toString();
                device.setDeviceIdentifier(generatedIdentifier);
                // device.setMqttPassword(generatedPassword);
                // device.setMqttUsername(GeneratedId);
                System.out.println("Generated Password: " + generatedPassword);
                // String hashedPassword = org.springframework.security.crypto.bcrypt.BCrypt.hashpw(generatedPassword,
                //                 org.springframework.security.crypto.bcrypt.BCrypt.gensalt());
                // // device.setMqttHashPassword(hashedPassword);

                Device savedDevice = deviceRepository.save(device);
                DeviceToken deviceToken = new DeviceToken();
                String Generatedtoken = UUID.randomUUID().toString() + "-" + System.currentTimeMillis();

                deviceToken.setDevice(savedDevice);
                deviceToken.setToken(Generatedtoken);
                this.deviceTokenRepo.save(deviceToken);
                
                deviceProducer.sendDeviceCreation(savedDevice);
                DeviceResponse deviceResponse = deviceMapper.toResponse(savedDevice);
                deviceResponse.setDeviceToken(Generatedtoken);
                return deviceResponse;
        }

        @Override
        public void deleteDevice(UUID deviceId) {
                this.deviceRepository.deleteById(deviceId);
                this.deviceProducer.sendDeviceDeleted(deviceId);
        }

        @Override
        @Transactional(readOnly = true)
        public DeviceResponse getDEviceById(UUID deviceId) {
                Optional<Device> device = this.deviceRepository.findById(deviceId);
                if (device.isEmpty()) {
                        throw new DeviceNotFoundException("device with ID " + deviceId + " not found");
                }
                DeviceResponse deviceReponse = this.deviceMapper.toResponse(device.get());
                return deviceReponse;
        }

        @Override
        public DeviceResponse updateDevice(UUID id, UpdateDeviceRequest updateDeviceRequest) {
                Device device = this.deviceRepository.findById(id).orElseThrow(
                                () -> new DeviceNotFoundException("device with ID " + id + " not found"));

                deviceMapper.updateDeviceFromRequest(updateDeviceRequest, device);
                Device updatedDevice = this.deviceRepository.save(device);
                deviceProducer.sendDeviceUpdate(updatedDevice);
                return deviceMapper.toResponse(updatedDevice);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<DeviceResponse> getAllDevices(Pageable pageable) {
                Page<DeviceResponse> devices = this.deviceRepository.findAll(pageable).map(deviceMapper::toResponse);
                return devices;
        }

        @Override
        public ProvisionResponse provisionDevice(
                        String token,
                        ProvisionRequest provisionRequest) {

                System.out.println("========================================");
                System.out.println("DEVICE PROVISIONING");
                System.out.println("========================================");

                System.out.println("Device info from Rust agent:::::::::::::::::::::::: "
                                + provisionRequest.getDeviceInfo().getOsName());

                DeviceToken deviceToken = deviceTokenRepo.findByToken(token)
                                .orElseThrow(() -> new DeviceNotFoundException(
                                                "Device token not found"));

                Device device = deviceToken.getDevice();
                DeviceInfo deviceInfo = provisionRequest.getDeviceInfo();
                device.setHostname(deviceInfo.getHostname());
                device.setIpAddress(deviceInfo.getIpAddress());
                device.setMacAddress(deviceInfo.getMacAddress());
                device.setOsName(deviceInfo.getOsName());
                device.setOsVersion(deviceInfo.getOsVersion());
                device.setKernelVersion(deviceInfo.getKernelVersion());
                device.setCpuCount(deviceInfo.getCpuCount());
                device.setTotalMemoryKb(deviceInfo.getTotalMemoryKb());
                deviceProducer.sendDeviceUpdate(device);
                System.out.println("===================================================" + device);
                System.out.println("Device ID: " + device.getId());
                System.out.println("Tenant ID: " + device.getTenantId());
                System.out.println("Hostname: " + device.getHostname());
                System.out.println("IP Address: " + device.getIpAddress());
                System.out.println("MAC Address: " + device.getMacAddress());
                System.out.println("OS Name: " + device.getOsName());
                System.out.println("OS Version: " + device.getOsVersion());
                System.out.println("Kernel Version: " + device.getKernelVersion());
                System.out.println("CPU Count: " + device.getCpuCount());
                System.out.println("Total Memory (KB): " + device.getTotalMemoryKb());
                System.out.println("Device info updated successfully.");
                

                if (provisionRequest == null || provisionRequest.getCsr() == null
                                || provisionRequest.getCsr().isBlank()) {

                        throw new IllegalArgumentException("CSR is required");
                }

                String csr = provisionRequest.getCsr();

                System.out.println("CSR received from Rust agent.");

                try {

                        String clientCertificate = certificateService.signCsr(
                                        csr,
                                        device.getId().toString());

                        System.out.println(
                                        "Device certificate signed successfully.");

                        String caCertificate = Files.readString(
                                        Path.of(
                                                        "C:/mqtt/backend/ca/ca.crt"));

                        ProvisionResponse response = new ProvisionResponse();

                        response.setDeviceId(
                                        device.getId().toString());

                        response.setTenantId(
                                        device.getTenantId().toString());

                        response.setCaCertificate(
                                        caCertificate);

                        response.setClientCertificate(
                                        clientCertificate);

                        System.out.println(
                                        "Provisioning response created.");

                        System.out.println("========================================");

                        return response;

                } catch (Exception e) {

                        System.err.println(
                                        "Certificate provisioning failed.");

                        e.printStackTrace();

                        throw new RuntimeException(
                                        "Failed to provision device certificate",
                                        e);
                }
        }
        // @Override
        // public ProvisionResponse provisionDevice(String token, ProvisionRequest
        // provisionRequest) {

        // System.out.println("Token from Rust agent::::::::::::::: " + token);
        // System.out.println("ProvisionRequest from Rust agent::::::::::::: " +
        // provisionRequest);
        // Optional<DeviceToken> deviceToken = deviceTokenRepo.findByToken(token);

        // if (deviceToken.isEmpty()) {
        // throw new DeviceNotFoundException("Device token not found");
        // }

        // Device device = deviceToken.get().getDevice();

        // ProvisionResponse response = new ProvisionResponse();

        // response.setDeviceId(device.getId().toString());
        // response.setTenantId(device.getTenantId().toString());

        // response.setCaCertificate("CA certificate");

        // response.setClientCertificate("client certificate");

        // return response;
        // }

}
