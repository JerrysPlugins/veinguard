/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.database;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMigrator {

    private final VeinGuard plugin;

    public DatabaseMigrator(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public void migrate(Database source, Database destination) throws SQLException {
        if (!source.isConnected()) {
            throw new SQLException("Source database is not connected.");
        }
        if (!destination.isConnected()) {
            throw new SQLException("Destination database is not connected.");
        }

        plugin.getLog().log(Level.INFO, "Starting database migration...");

        destination.initialize();
        new DatabaseSchemaUpdater(plugin).update(destination);

        String prefix = plugin.getConfigOptions().getDbTablePrefix();
        migrateAlerts(source, destination, prefix);
        migrateViolations(source, destination, prefix);

        plugin.getLog().log(Level.SUCCESS, "Database migration completed successfully!");
    }

    private void migrateAlerts(Database source, Database destination, String prefix) throws SQLException {
        String tableName = (prefix == null || prefix.isBlank()) ? "vg_alerts" : prefix + "alerts";
        String selectQuery = "SELECT * FROM " + tableName;
        String insertQuery = "INSERT INTO " + tableName + " (uuid, player_name, material, count, world, x, y, z, last_x, last_y, last_z, timestamp, last_timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        int count = 0;
        try (PreparedStatement selectStmt = source.getConnection().prepareStatement(selectQuery);
             ResultSet rs = selectStmt.executeQuery();
             PreparedStatement insertStmt = destination.getConnection().prepareStatement(insertQuery)) {

            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            boolean hasLastCols = false;
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (metaData.getColumnName(i).equalsIgnoreCase("last_timestamp")) {
                    hasLastCols = true;
                    break;
                }
            }

            boolean originalAutoCommit = destination.getConnection().getAutoCommit();
            destination.getConnection().setAutoCommit(false);

            try {
                while (rs.next()) {
                    insertStmt.setString(1, rs.getString("uuid"));
                    insertStmt.setString(2, rs.getString("player_name"));
                    insertStmt.setString(3, rs.getString("material"));
                    insertStmt.setInt(4, rs.getInt("count"));
                    insertStmt.setString(5, rs.getString("world"));

                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    insertStmt.setInt(6, x);
                    insertStmt.setInt(7, y);
                    insertStmt.setInt(8, z);

                    if (hasLastCols) {
                        insertStmt.setInt(9, rs.getInt("last_x"));
                        insertStmt.setInt(10, rs.getInt("last_y"));
                        insertStmt.setInt(11, rs.getInt("last_z"));
                    } else {
                        insertStmt.setInt(9, x);
                        insertStmt.setInt(10, y);
                        insertStmt.setInt(11, z);
                    }

                    long timestamp = source.getTimestamp(rs, "timestamp");
                    destination.prepareTimestamp(insertStmt, 12, timestamp);

                    if (hasLastCols) {
                        destination.prepareTimestamp(insertStmt, 13, source.getTimestamp(rs, "last_timestamp"));
                    } else {
                        destination.prepareTimestamp(insertStmt, 13, timestamp);
                    }

                    insertStmt.addBatch();
                    count++;

                    if (count % 1000 == 0) {
                        insertStmt.executeBatch();
                        destination.getConnection().commit();
                    }
                }
                insertStmt.executeBatch();
                destination.getConnection().commit();
                plugin.getLog().log(Level.INFO, "Migrated " + count + " alert records.");
            } catch (SQLException e) {
                destination.getConnection().rollback();
                throw e;
            } finally {
                destination.getConnection().setAutoCommit(originalAutoCommit);
            }
        }
    }

    private void migrateViolations(Database source, Database destination, String prefix) throws SQLException {
        String tableName = (prefix == null || prefix.isBlank()) ? "vg_violations" : prefix + "violations";
        String selectQuery = "SELECT uuid, violation_level, last_update FROM " + tableName;
        String insertQuery = "INSERT INTO " + tableName + " (uuid, violation_level, last_update) VALUES (?, ?, ?)";

        int count = 0;
        try (PreparedStatement selectStmt = source.getConnection().prepareStatement(selectQuery);
             ResultSet rs = selectStmt.executeQuery();
             PreparedStatement insertStmt = destination.getConnection().prepareStatement(insertQuery)) {

            boolean originalAutoCommit = destination.getConnection().getAutoCommit();
            destination.getConnection().setAutoCommit(false);

            try {
                while (rs.next()) {
                    insertStmt.setString(1, rs.getString("uuid"));
                    insertStmt.setDouble(2, rs.getDouble("violation_level"));
                    insertStmt.setLong(3, rs.getLong("last_update"));

                    insertStmt.addBatch();
                    count++;

                    if (count % 1000 == 0) {
                        insertStmt.executeBatch();
                        destination.getConnection().commit();
                    }
                }
                insertStmt.executeBatch();
                destination.getConnection().commit();
                plugin.getLog().log(Level.INFO, "Migrated " + count + " violation records.");
            } catch (SQLException e) {

                destination.getConnection().rollback();
                plugin.getLog().log(Level.WARN, "Could not migrate violations (table might not exist in source).");
            } finally {
                destination.getConnection().setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {

            plugin.getLog().log(Level.WARN, "Could not migrate violations: " + e.getMessage());
        }
    }
}
