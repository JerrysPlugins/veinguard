/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.update;

public final class VersionComparison {

    private VersionComparison() {}

    public static int compare(String currentVersion, String latestVersion) {
        int current = simplifyVersion(currentVersion);
        int latest = simplifyVersion(latestVersion);
        return Integer.compare(current, latest);
    }

    public static BuildType getBuildType(String version) {
        return BuildType.fromVersionSubString(version);
    }

    public static VersionStatus getVersionStatus(String currentVersion, String latestVersion) {
        int comparison = compare(currentVersion, latestVersion);

        if (comparison < 0) return VersionStatus.BEHIND;
        if (comparison > 0) return VersionStatus.AHEAD;
        return VersionStatus.UP_TO_DATE;
    }

    private static int simplifyVersion(String version) {
        if (version == null || version.isEmpty()) return 0;

        String numeric = version.split("-")[0];
        String[] parts = numeric.split("\\.");

        int major = parts.length > 0 ? parsePart(parts[0]) : 0;
        int minor = parts.length > 1 ? parsePart(parts[1]) : 0;
        int patch = parts.length > 2 ? parsePart(parts[2]) : 0;

        return (major * 1_000_000) + (minor * 1_000) + patch;
    }

    private static int parsePart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}