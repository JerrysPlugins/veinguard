/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.logger;

public enum Level {

    FATAL(8),
    ERROR(7),
    WARN(6),
    INFO(5),
    SUCCESS(4),
    UPDATE(3),
    LOG(2),
    DEBUG(1),
    NONE(0);

    private final int severityId;

    Level(int severityId) {
        this.severityId = severityId;
    }
}
