/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
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

    public static long parseTimeStringToMillis(String timeString) {
        if (timeString == null || timeString.isBlank()) return -1;

        long totalMillis = 0;
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)([smhd])");
        java.util.regex.Matcher matcher = pattern.matcher(timeString.toLowerCase());

        boolean matched = false;
        while (matcher.find()) {
            matched = true;
            long amount = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "s" -> totalMillis += amount * 1000L;
                case "m" -> totalMillis += amount * 60L * 1000L;
                case "h" -> totalMillis += amount * 60L * 60L * 1000L;
                case "d" -> totalMillis += amount * 24L * 60L * 60L * 1000L;
            }
        }

        return matched ? totalMillis : -1;
    }
}
