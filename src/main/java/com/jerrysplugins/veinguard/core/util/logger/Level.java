package com.jerrysplugins.veinguard.core.util.logger;

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

    public int getSeverityId() {
        return this.severityId;
    }

    public static Level fromSeverityId(int severityId) {
        for (Level level : values()) {
            if(level.severityId == severityId) return level;
        }
        return null;
    }
}