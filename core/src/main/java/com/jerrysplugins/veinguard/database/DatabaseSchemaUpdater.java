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
    private static final int CURRENT_SCHEMA_VERSION = 4;

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
            case 2:
                if (database instanceof MySQLDatabase) {
                    String tableName = (prefix == null || prefix.isBlank()) ? "vg_alerts" : prefix + "alerts";
                    try (Statement stmt = database.getConnection().createStatement()) {

                        boolean isBigInt = false;
                        DatabaseMetaData meta = database.getConnection().getMetaData();
                        try (ResultSet rs = meta.getColumns(null, null, tableName, "timestamp")) {
                            if (rs.next()) {
                                String typeName = rs.getString("TYPE_NAME");
                                if ("BIGINT".equalsIgnoreCase(typeName)) {
                                    isBigInt = true;
                                }
                            }
                        }

                        if (!isBigInt) {
                            plugin.getLog().log(Level.INFO, "Migrating MySQL timestamp column to BIGINT...");
                            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN timestamp_new BIGINT AFTER z");
                            stmt.execute("UPDATE " + tableName + " SET timestamp_new = UNIX_TIMESTAMP(timestamp) * 1000");
                            stmt.execute("ALTER TABLE " + tableName + " DROP COLUMN timestamp");
                            stmt.execute("ALTER TABLE " + tableName + " CHANGE timestamp_new timestamp BIGINT");
                        }
                    }
                }
                break;
            case 3:
                String violationsTable = (prefix == null || prefix.isBlank()) ? "vg_violations" : prefix + "violations";
                try (Statement stmt = database.getConnection().createStatement()) {
                    if (database instanceof SQLiteDatabase) {
                        stmt.execute("CREATE TABLE IF NOT EXISTS " + violationsTable + " (" +
                                "uuid TEXT PRIMARY KEY," +
                                "violation_level REAL NOT NULL," +
                                "last_update BIGINT NOT NULL" +
                                ")");
                    } else {
                        stmt.execute("CREATE TABLE IF NOT EXISTS " + violationsTable + " (" +
                                "uuid VARCHAR(36) PRIMARY KEY," +
                                "violation_level DOUBLE NOT NULL," +
                                "last_update BIGINT NOT NULL" +
                                ")");
                    }
                }
                break;
            case 4:
                String alertsTable = (prefix == null || prefix.isBlank()) ? "vg_alerts" : prefix + "alerts";
                addColumnIfNotExists(database, alertsTable, "last_x", "INTEGER DEFAULT 0");
                addColumnIfNotExists(database, alertsTable, "last_y", "INTEGER DEFAULT 0");
                addColumnIfNotExists(database, alertsTable, "last_z", "INTEGER DEFAULT 0");
                addColumnIfNotExists(database, alertsTable, "last_timestamp", "BIGINT DEFAULT 0");

                try (Statement stmt = database.getConnection().createStatement()) {
                    stmt.execute("UPDATE " + alertsTable + " SET last_timestamp = timestamp WHERE last_timestamp = 0");
                    stmt.execute("UPDATE " + alertsTable + " SET last_x = x WHERE last_x = 0");
                    stmt.execute("UPDATE " + alertsTable + " SET last_y = y WHERE last_y = 0");
                    stmt.execute("UPDATE " + alertsTable + " SET last_z = z WHERE last_z = 0");
                }
                break;
        }
    }

    private void addColumnIfNotExists(Database database, String tableName, String columnName, String columnDefinition) throws SQLException {
        if (!columnExists(database, tableName, columnName)) {
            try (Statement stmt = database.getConnection().createStatement()) {
                stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition);
            }
        }
    }

    private boolean columnExists(Database database, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = database.getConnection().getMetaData();

        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            if (rs.next()) return true;
        }

        try (ResultSet rs = meta.getColumns(null, null, tableName.toUpperCase(), columnName.toUpperCase())) {
            return rs.next();
        }
    }

    private boolean tableExists(Database database, String tableName) throws SQLException {
        DatabaseMetaData meta = database.getConnection().getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            if (rs.next()) return true;
        }

        try (ResultSet rs = meta.getTables(null, null, tableName.toUpperCase(), new String[]{"TABLE"})) {
            return rs.next();
        }
    }
}
