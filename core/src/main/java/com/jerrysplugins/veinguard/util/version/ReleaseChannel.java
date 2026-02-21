/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.version;

public enum ReleaseChannel {

    STABLE("", "Stable"),
    BETA("-BETA", "Beta"),
    ALPHA("-ALPHA", "Alpha"),
    PRE_RELEASE("-PRE_RELEASE", "Pre-Release"),
    SNAPSHOT("-SNAPSHOT", "Snapshot"),
    EXPERIMENTAL("-EXPERIMENTAL", "Experimental"),
    UNKNOWN("", "Unknown");

    private final String versionChannelString;
    private final String displayName;

    ReleaseChannel(String versionChannelString, String displayName) {
        this.versionChannelString = versionChannelString;
        this.displayName = displayName;
    }

    public String getDisplayName() { return this.displayName; }

    public static ReleaseChannel fromVersionString(String versionChannelString) {

        if(versionChannelString == null || versionChannelString.isBlank()) {
            return STABLE;
        }

        String normalized = versionChannelString.toUpperCase();

        for (ReleaseChannel channel : new ReleaseChannel[] {
                BETA, ALPHA, PRE_RELEASE, SNAPSHOT, EXPERIMENTAL
        }) {
            if (normalized.contains(channel.versionChannelString)) {
                return channel;
            }
        }

        return UNKNOWN;
    }
}