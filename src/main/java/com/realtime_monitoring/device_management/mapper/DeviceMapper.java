package com.realtime_monitoring.device_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.realtime_monitoring.device_management.dto.CreateDeviecRequest;
import com.realtime_monitoring.device_management.dto.DeviceResponse;
import com.realtime_monitoring.device_management.dto.UpdateDeviceRequest;
import com.realtime_monitoring.device_management.entity.Device;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DeviceMapper {
    Device toEntity(CreateDeviecRequest createDeviceRequest);
    DeviceResponse toResponse(Device device);
    Device updateDeviceFromRequest(UpdateDeviceRequest updateDeviceRequest, Device device);
}
