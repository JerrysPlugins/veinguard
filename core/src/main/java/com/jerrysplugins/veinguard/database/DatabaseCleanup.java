/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.database;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DatabaseCleanup {

    private final VeinGuard plugin;
    private BukkitTask cleanupTask;

    public DatabaseCleanup(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.getConfigOptions().isDbCleanupEnabled()) {
            return;
        }

        long intervalTicks = plugin.getConfigOptions().getDbCleanupInterval() * 20L;
        String retention = plugin.getConfigOptions().getDbCleanupRetention();

        plugin.getLog().log(Level.INFO, "Starting database cleanup task (Interval: " +
                plugin.getConfigOptions().getDbCleanupInterval() + "s, Retention: " + retention + ")");

        cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getLog().log(Level.DEBUG, "Running automated database cleanup...");
                new DatabasePurge(plugin).purgeAsync(retention).thenAccept(deleted -> {
                    if (deleted > 0) {
                        plugin.getLog().log(Level.SUCCESS, "Automated database cleanup completed. Deleted " + deleted + " old records.");
                    } else {
                        plugin.getLog().log(Level.DEBUG, "Automated database cleanup completed. No records were deleted.");
                    }
                });
            }
        }.runTaskTimerAsynchronously(plugin, intervalTicks, intervalTicks);
    }

    public void stop() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
    }
}
