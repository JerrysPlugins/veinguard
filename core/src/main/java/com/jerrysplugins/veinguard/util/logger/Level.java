/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.logger;

public enum Level {

    FATAL(8),      // Critical plugin failure; may disable plugin
    ERROR(7),      // Serious issues/exceptions
    WARN(6),       // Recoverable warnings
    INFO(5),       // General plugin operations
    SUCCESS(4),    // Positive actions to highlight (like plugin enabled)
    UPDATE(3),     // Update notifications
    LOG(2),        // Generic logs not needing a specific level
    DEBUG(1),      // Development-only verbose info
    NONE(0);       // No level prefix

    private final int severityId;

    Level(int severityId) {
        this.severityId = severityId;
    }
}