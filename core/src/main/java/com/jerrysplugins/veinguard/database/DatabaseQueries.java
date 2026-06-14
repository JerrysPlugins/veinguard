/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.database;

import com.jerrysplugins.veinguard.VeinGuard;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseQueries {

    private final VeinGuard plugin;

    public DatabaseQueries(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public List<TopEntry> getTopPlayers(long sinceMillis) {
        List<TopEntry> topPlayers = new ArrayList<>();
        Database database = plugin.getVGDatabase();

        if (database == null || !database.isConnected()) {
            return topPlayers;
        }

        String prefix = plugin.getConfigOptions().getDbTablePrefix();
        String tableName = (prefix == null || prefix.isBlank()) ? "vg_alerts" : prefix + "alerts";

        StringBuilder queryBuilder = new StringBuilder("SELECT uuid, MAX(player_name) as player_name, SUM(count) as total_count, COUNT(*) as alert_count FROM ");
        queryBuilder.append(tableName);

        if (sinceMillis > 0) {
            queryBuilder.append(" WHERE last_timestamp >= ?");
        }

        queryBuilder.append(" GROUP BY uuid ORDER BY total_count DESC");

        try (PreparedStatement ps = database.getConnection().prepareStatement(queryBuilder.toString())) {
            if (sinceMillis > 0) {
                database.prepareTimestamp(ps, 1, sinceMillis);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String uuidStr = rs.getString("uuid");
                    String name = rs.getString("player_name");
                    int count = rs.getInt("total_count");
                    int alertCount = rs.getInt("alert_count");

                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        topPlayers.add(new TopEntry(name, uuid, count, alertCount));
                    } catch (IllegalArgumentException e) {
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topPlayers;
    }

    public record TopEntry(String playerName, UUID uuid, int totalBlocks, int alertCount) {}

    public List<AlertHistoryEntry> getPlayerAlertHistory(String nameOrUuid, long sinceMillis) {
        List<AlertHistoryEntry> history = new ArrayList<>();
        Database database = plugin.getVGDatabase();

        if (database == null || !database.isConnected()) {
            return history;
        }

        String prefix = plugin.getConfigOptions().getDbTablePrefix();
        String tableName = (prefix == null || prefix.isBlank()) ? "vg_alerts" : prefix + "alerts";

        StringBuilder queryBuilder = new StringBuilder("SELECT material, count, world, x, y, z, last_x, last_y, last_z, timestamp, last_timestamp FROM ");
        queryBuilder.append(tableName);
        queryBuilder.append(" WHERE (uuid = ? OR player_name = ?)");

        if (sinceMillis > 0) {
            queryBuilder.append(" AND last_timestamp >= ?");
        }

        queryBuilder.append(" ORDER BY last_timestamp DESC");

        try (PreparedStatement ps = database.getConnection().prepareStatement(queryBuilder.toString())) {
            ps.setString(1, nameOrUuid);
            ps.setString(2, nameOrUuid);
            if (sinceMillis > 0) {
                database.prepareTimestamp(ps, 3, sinceMillis);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String material = rs.getString("material");
                    int count = rs.getInt("count");
                    String world = rs.getString("world");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");
                    int lastX = rs.getInt("last_x");
                    int lastY = rs.getInt("last_y");
                    int lastZ = rs.getInt("last_z");
                    long timestamp = database.getTimestamp(rs, "timestamp");
                    long lastTimestamp = database.getTimestamp(rs, "last_timestamp");

                    history.add(new AlertHistoryEntry(material, count, world, x, y, z, lastX, lastY, lastZ, timestamp, lastTimestamp));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    public record AlertHistoryEntry(String material, int count, String world, int x, int y, int z, int lastX, int lastY, int lastZ, long timestamp, long lastTimestamp) {}
}
