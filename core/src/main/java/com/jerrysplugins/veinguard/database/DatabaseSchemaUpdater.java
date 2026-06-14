/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.database;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;

import java.sql.*;

public class DatabaseSchemaUpdater {

    private final VeinGuard plugin;
    private static final int CURRENT_SCHEMA_VERSION = 1;

    public DatabaseSchemaUpdater(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public void update(Database database) {
        if (!database.isConnected()) return;

        String prefix = plugin.getConfigOptions().getDbTablePrefix();
        String versionTable = (prefix == null || prefix.isBlank()) ? "vg_schema_version" : prefix + "schema_version";

        try {
            ensureVersionTableExists(database, versionTable);
            int currentVersion = getCurrentVersion(database, versionTable);

            if (currentVersion < CURRENT_SCHEMA_VERSION) {
                plugin.getLog().log(Level.INFO, "Updating database schema from version " + currentVersion + " to " + CURRENT_SCHEMA_VERSION + "...");

                for (int v = currentVersion + 1; v <= CURRENT_SCHEMA_VERSION; v++) {
                    applyMigration(database, v, prefix);
                }

                updateVersion(database, versionTable, CURRENT_SCHEMA_VERSION);
                plugin.getLog().log(Level.SUCCESS, "Database schema updated successfully to version " + CURRENT_SCHEMA_VERSION + ".");
            }
        } catch (SQLException e) {
            plugin.getLog().log(Level.ERROR, "Failed to update database schema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ensureVersionTableExists(Database database, String tableName) throws SQLException {
        try (Statement stmt = database.getConnection().createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (version INTEGER NOT NULL)");

            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO " + tableName + " (version) VALUES (0)");
                }
            }
        }
    }

    private int getCurrentVersion(Database database, String tableName) throws SQLException {
        try (Statement stmt = database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT version FROM " + tableName + " LIMIT 1")) {
            if (rs.next()) {
                return rs.getInt("version");
            }
        }
        return 0;
    }

    private void updateVersion(Database database, String tableName, int version) throws SQLException {
        try (PreparedStatement ps = database.getConnection().prepareStatement("UPDATE " + tableName + " SET version = ?")) {
            ps.setInt(1, version);
            ps.executeUpdate();
        }
    }

    private void applyMigration(Database database, int targetVersion, String prefix) throws SQLException {
        switch (targetVersion) {
            case 1:
                break;
        }
    }
}