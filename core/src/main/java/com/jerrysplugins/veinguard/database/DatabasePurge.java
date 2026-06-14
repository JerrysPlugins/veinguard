/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.database;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.VGUtils;
import com.jerrysplugins.veinguard.util.logger.Level;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabasePurge {

    private final VeinGuard plugin;

    public DatabasePurge(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<Integer> purgeAsync(String timeString) {
        long durationMillis = VGUtils.parseTimeStringToMillis(timeString);

        if (durationMillis <= 0) {
            plugin.getLog().log(Level.ERROR, "Invalid time format for purge: " + timeString);
            return CompletableFuture.completedFuture(0);
        }

        long cutoffTimestamp = System.currentTimeMillis() - durationMillis;

        return CompletableFuture.supplyAsync(() -> {
            Database database = plugin.getVGDatabase();
            if (database == null || !database.isConnected()) {
                plugin.getLog().log(Level.ERROR, "Cannot purge: Database is not connected.");
                return 0;
            }

            String prefix = plugin.getConfigOptions().getDbTablePrefix();
            int deleted = database.purgeAlerts(cutoffTimestamp, prefix);

            if (deleted > 0) {
                plugin.getLog().log(Level.SUCCESS, "Purged " + deleted + " records older than " + timeString + " from the database.");
            } else {
                plugin.getLog().log(Level.INFO, "No records older than " + timeString + " were found to purge.");
            }

            return deleted;
        });
    }

    public CompletableFuture<Integer> purgeAsync(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            Database database = plugin.getVGDatabase();
            if (database == null || !database.isConnected()) {
                plugin.getLog().log(Level.ERROR, "Cannot purge: Database is not connected.");
                return 0;
            }

            String prefix = plugin.getConfigOptions().getDbTablePrefix();
            int deleted = database.purgeAlerts(uuid, prefix);

            if (deleted > 0) {
                plugin.getLog().log(Level.SUCCESS, "Purged " + deleted + " records for player " + uuid + " from the database.");
            } else {
                plugin.getLog().log(Level.INFO, "No records for player " + uuid + " were found to purge.");
            }

            return deleted;
        });
    }

    public CompletableFuture<Integer> purgeAsync(UUID uuid, String timeString) {
        long durationMillis = VGUtils.parseTimeStringToMillis(timeString);

        if (durationMillis <= 0) {
            plugin.getLog().log(Level.ERROR, "Invalid time format for purge: " + timeString);
            return CompletableFuture.completedFuture(0);
        }

        long cutoffTimestamp = System.currentTimeMillis() - durationMillis;

        return CompletableFuture.supplyAsync(() -> {
            Database database = plugin.getVGDatabase();
            if (database == null || !database.isConnected()) {
                plugin.getLog().log(Level.ERROR, "Cannot purge: Database is not connected.");
                return 0;
            }

            String prefix = plugin.getConfigOptions().getDbTablePrefix();
            int deleted = database.purgeAlerts(uuid, cutoffTimestamp, prefix);

            if (deleted > 0) {
                plugin.getLog().log(Level.SUCCESS, "Purged " + deleted + " records older than " + timeString + " for player " + uuid + " from the database.");
            } else {
                plugin.getLog().log(Level.INFO, "No records older than " + timeString + " for player " + uuid + " were found to purge.");
            }

            return deleted;
        });
    }
}
