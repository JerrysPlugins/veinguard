/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.logger;

import com.jerrysplugins.veinguard.VeinGuard;
import org.bukkit.Bukkit;

import java.util.EnumMap;
import java.util.Map;

public class Logger {

    private final VeinGuard plugin;

    private String pluginPrefix;
    private final Map<Level, String> logPrefixes;
    private boolean debugEnabled = false;

    public Logger(VeinGuard plugin) {
        this.plugin = plugin;
        this.logPrefixes = new EnumMap<>(Level.class);
        setLogPrefixes();
    }

    public void log(Level level, String message) {
        log(level, message, null);
    }

    public void log(Level level, String message, Throwable throwable) {
        if(level == null || message == null) return;
        if(level == Level.DEBUG && !debugEnabled) return;

        String levelPrefix = logPrefixes.get(level);
        if(levelPrefix == null) return;

        if(throwable == null) {
            sendLog(levelPrefix, message);
        } else {
            sendLogThrowable(levelPrefix, message, throwable);
        }
    }

    private void sendLog(String levelPrefix, String message) {
        Bukkit.getConsoleSender().sendMessage(pluginPrefix + levelPrefix + message);
    }

    private void sendLogThrowable(String levelPrefix, String message, Throwable throwable) {
        Bukkit.getConsoleSender().sendMessage(pluginPrefix + levelPrefix + message);

        Bukkit.getConsoleSender().sendMessage("    " + throwable);
        for (StackTraceElement element : throwable.getStackTrace()) {
            Bukkit.getConsoleSender().sendMessage("        at " + element);
        }

        Throwable cause = throwable.getCause();
        if (cause != null) {
            sendLogThrowable(levelPrefix, "Caused by: " + cause, cause);
        }
    }

    private void setLogPrefixes() {
        pluginPrefix = "[" + plugin.getName() + "]";
        for(Level level : Level.values()) {
            if(level.equals(Level.NONE)) {
                logPrefixes.put(level, ": ");
                continue;
            }
            if(level.equals(Level.DEBUG)) {
                logPrefixes.put(level, " * [DEBUG] ");
                continue;
            }
            logPrefixes.put(level, "[" + level.name().toUpperCase() + "] ");
        }
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
        log(Level.DEBUG, "Debug mode is enabled! Console will now show debug logging.");
    }

    public boolean isDebugConfigEnabled() {
        return plugin.getVGConfig().getBoolean("debug-mode", false);
    }
}