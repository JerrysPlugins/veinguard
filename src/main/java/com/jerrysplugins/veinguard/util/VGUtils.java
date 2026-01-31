package com.jerrysplugins.veinguard.util;

import org.bukkit.Location;

import java.util.Map;

public class VGUtils {

    public static String applyPlaceholders(final String message, final Map<String, String> placeholders) {
        if (message == null || message.isBlank() || placeholders == null) return message;

        String result = message;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && !key.isEmpty() && value != null) {
                result = result.replace(key, value);
            }
        }

        return result;
    }

    public static String getPrettyLocation(Location location) {
        if (location == null || location.getWorld() == null) return "Unknown location.";

        return String.format(
                "(%s), X: %d, Y: %d, Z: %d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }
}