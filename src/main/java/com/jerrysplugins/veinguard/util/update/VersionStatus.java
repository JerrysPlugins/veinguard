/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.update;

public enum VersionStatus {
    UP_TO_DATE,   // Current version matches latest
    BEHIND,       // Current version is older than latest
    AHEAD         // Current version is newer than latest (developmental)
}