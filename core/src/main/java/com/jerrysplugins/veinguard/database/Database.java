/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public abstract class Database {

    protected Connection connection;

    public abstract void connect() throws SQLException;

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public abstract void initialize();

    public void prepareTimestamp(PreparedStatement ps, int index, long millis) throws SQLException {
        ps.setLong(index, millis);
    }

    public long getTimestamp(java.sql.ResultSet rs, String columnName) throws SQLException {
        Object obj = rs.getObject(columnName);
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        java.sql.Timestamp ts = rs.getTimestamp(columnName);
        return ts != null ? ts.getTime() : 0;
    }

    public void logAlert(UUID uuid, String playerName, String material, int count, String world, int x, int y, int z, long timestamp, String tablePrefix) {
        String tableName = (tablePrefix == null || tablePrefix.isBlank()) ? "vg_alerts" : tablePrefix + "alerts";
        String query = "INSERT INTO " + tableName + " (uuid, player_name, material, count, world, x, y, z, last_x, last_y, last_z, timestamp, last_timestamp) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, playerName);
            ps.setString(3, material);
            ps.setInt(4, count);
            ps.setString(5, world);
            ps.setInt(6, x);
            ps.setInt(7, y);
            ps.setInt(8, z);
            ps.setInt(9, x);
            ps.setInt(10, y);
            ps.setInt(11, z);
            prepareTimestamp(ps, 12, timestamp);
            prepareTimestamp(ps, 13, timestamp);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void logAlertToIncident(UUID uuid, String playerName, String material, int count, String world, int x, int y, int z, long timestamp, long checkIntervalMs, String tablePrefix) {
        String tableName = (tablePrefix == null || tablePrefix.isBlank()) ? "vg_alerts" : tablePrefix + "alerts";

        String findQuery = "SELECT id FROM " + tableName +
                " WHERE uuid = ? AND material = ? AND last_timestamp >= ?" +
                " ORDER BY last_timestamp DESC LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(findQuery)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, material);
            ps.setLong(3, timestamp - checkIntervalMs);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");

                    String updateQuery = "UPDATE " + tableName + " SET count = ?, last_x = ?, last_y = ?, last_z = ?, last_timestamp = ? WHERE id = ?";
                    try (PreparedStatement updatePs = connection.prepareStatement(updateQuery)) {
                        updatePs.setInt(1, count);
                        updatePs.setInt(2, x);
                        updatePs.setInt(3, y);
                        updatePs.setInt(4, z);
                        prepareTimestamp(updatePs, 5, timestamp);
                        updatePs.setInt(6, id);
                        updatePs.executeUpdate();
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        logAlert(uuid, playerName, material, count, world, x, y, z, timestamp, tablePrefix);
    }

    public int purgeAlerts(long olderThanMillis, String tablePrefix) {
        String tableName = (tablePrefix == null || tablePrefix.isBlank()) ? "vg_alerts" : tablePrefix + "alerts";
        String query = "DELETE FROM " + tableName + " WHERE last_timestamp < ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            prepareTimestamp(ps, 1, olderThanMillis);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int purgeAlerts(UUID uuid, String tablePrefix) {
        String tableName = (tablePrefix == null || tablePrefix.isBlank()) ? "vg_alerts" : tablePrefix + "alerts";
        String query = "DELETE FROM " + tableName + " WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int purgeAlerts(UUID uuid, long olderThanMillis, String tablePrefix) {
        String tableName = (tablePrefix == null || tablePrefix.isBlank()) ? "vg_alerts" : tablePrefix + "alerts";
        String query = "DELETE FROM " + tableName + " WHERE uuid = ? AND last_timestamp < ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            prepareTimestamp(ps, 2, olderThanMillis);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double loadViolationLevel(UUID uuid, String tablePrefix) {
        String tableName = (tablePrefix == null || tablePrefix.isBlank()) ? "vg_violations" : tablePrefix + "violations";
        String query = "SELECT violation_level FROM " + tableName + " WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("violation_level");
                }
            }
        } catch (SQLException e) {

        }
        return 0.0;
    }

    public abstract void saveViolationLevel(UUID uuid, double vl, String tablePrefix);
}
