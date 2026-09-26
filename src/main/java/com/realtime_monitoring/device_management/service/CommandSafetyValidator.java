package com.realtime_monitoring.device_management.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class CommandSafetyValidator {

    private static final Pattern SAFE_PATH = Pattern.compile(
            "^(?:/[A-Za-z0-9_.-]+)+(?:/[A-Za-z0-9_.-]+)*$");
    private static final Pattern SAFE_SERVICE = Pattern.compile(
            "^[A-Za-z0-9_.@:-]+\\.service$");

            
    public boolean isAllowed(String command) {
        if (command == null || command.isBlank()
                || command.indexOf('\0') >= 0
                || command.matches(".*[;&|<>`].*")) {
            return false;
        }

        String[] parts = command.trim().split("\\s+");
        String executable = parts[0];

        return switch (executable) {
            case "uptime" -> parts.length == 1;
            case "free", "df" -> matches(parts, "-h");
            case "ps" -> matches(parts, "aux") || matches(parts, "-ef");
            case "ls" -> isLs(parts);
            case "stat" -> parts.length == 2 && isSafePath(parts[1]);
            case "systemctl" -> isSystemctl(parts);
            case "journalctl" -> isJournalctl(parts);
            default -> false;
        };
    }

    private boolean matches(String[] parts, String argument) {
        return parts.length == 2 && argument.equals(parts[1]);
    }

    private boolean isLs(String[] parts) {
        if (parts.length < 2 || parts.length > 3) {
            return false;
        }

        boolean hasPath = false;
        for (int index = 1; index < parts.length; index++) {
            String part = parts[index];
            if (part.equals("-l") || part.equals("-la") || part.equals("-a")) {
                continue;
            }
            if (!isSafePath(part)) {
                return false;
            }
            hasPath = true;
        }
        return hasPath;
    }

    private boolean isSystemctl(String[] parts) {
        return (parts.length == 2 || parts.length == 3)
                && (parts[1].equals("status")
                || parts[1].equals("is-active")
                || parts[1].equals("is-failed"))
                && (parts.length == 2 || isSafeService(parts[2]));
    }

    private boolean isJournalctl(String[] parts) {
        return parts.length == 6
                && parts[1].equals("-u")
                && isSafeService(parts[2])
                && parts[3].equals("-n")
                && parts[4].matches("[1-9][0-9]*")
                && parts[5].equals("--no-pager");
    }

    private boolean isSafePath(String value) {
        return SAFE_PATH.matcher(value).matches();
    }

    private boolean isSafeService(String value) {
        return SAFE_SERVICE.matcher(value).matches();
    }
}