package com.realtime_monitoring.device_management.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Data
@Getter
@Setter
@Slf4j
public class ProvisionRequest {

    private String csr;
}
