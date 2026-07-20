package com.realtime_monitoring.device_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import com.realtime_monitoring.device_management.dto.CreateDeviecRequest;
import com.realtime_monitoring.device_management.dto.DeviceResponse;
import com.realtime_monitoring.device_management.dto.UpdateDeviceRequest;
import com.realtime_monitoring.device_management.entity.Device;
import org.mapstruct.MappingTarget;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public interface DeviceMapper {
    Device toEntity(CreateDeviecRequest createDeviceRequest);
    DeviceResponse toResponse(Device device);
    void updateDeviceFromRequest(UpdateDeviceRequest updateDeviceRequest,@MappingTarget Device device);
}
