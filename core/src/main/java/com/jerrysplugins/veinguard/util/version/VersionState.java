/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.version;

public enum VersionState {

    AHEAD("Ahead"),
    BEHIND("Behind"),
    UP_TO_DATE("Up To Date"),
    UNKNOWN("Unknown");

    private final String displayName;

    VersionState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return this.displayName; }
}