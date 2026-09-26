package com.realtime_monitoring.device_management.dto;

import lombok.Data;

@Data
public class DeviceInfo {
    String hostname;
    String ipAddress;
    String macAddress;
    String osName;
    String osVersion;
    String kernelVersion;
    Long cpuCount;
    Long totalMemoryKb;
}


// {"incidentId": "925a9bdf-3554-4279-a487-82a4d1cc140e", "deviceId": "3b1ae99f-dcab-4f31-9008-1af83dbcffe0", 
// "tenantId": "247690cf-eb39-4d1a-a4b4-c89093abf020", "severity": "HIGH", 
// "problem": "Application error due to missing required file
//  '/home/chedly/testchedly.txt'.", "rootCause": "An active process, script, or
//   scheduled task (cron/systemd unit) attempted to access '/home/chedly/testchedly.txt',
//    but the file is missing, deleted, or incorrectly named.", "confidence": 0.95,
//     "impact": "The calling application or script fails to execute its intended task,
//      resulting in application logs reporting unhandled file access errors.", 
//      "recommendations": [{"priority": 1, "action": "Verify if '/home/chedly/testchedly.txt' 
//      or similar files exist and check directory contents and permissions.", 
//      "command": "ls -la /home/chedly/", "execution_status": "REJECTED", 
//      "execution_output": null, "execution_error": "Command rejected by safety validator"}
//      , {"priority": 2, "action": "Search crontabs, systemd services, and active user 
//      processes for references to 'testchedly.txt' to identify the caller.", "command": 
//      "grep -rn \"testchedly.txt\" /etc/cron* /var/spool/cron/crontabs/ /etc/systemd
//      /system/ /home/chedly/ 2>/dev/null", "execution_status": "REJECTED",
//       "execution_output": null, "execution_error": "Command rejected by safety validator"},
//        {"priority": 3, "action": "Recreate the missing file with correct ownership and permissions if required by the sc
//        ript or process.",
//         "command": "touch /home/chedly/testchedly.txt && chown chedly:chedly /home/chedly/testchedly.txt", "execution_status":
//  "REJECTED", "execution_output": null, 
//  "execution_error": "Command rejected by safety validator"}]}