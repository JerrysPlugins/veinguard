/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.database;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class MySQLDatabase extends Database {

    private final VeinGuard plugin;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQLDatabase(VeinGuard plugin) {
        this.plugin = plugin;
        this.host = plugin.getConfigOptions().getDbHost();
        this.port = plugin.getConfigOptions().getDbPort();
        this.database = plugin.getConfigOptions().getDbName();
        this.username = plugin.getConfigOptions().getDbUsername();
        this.password = plugin.getConfigOptions().getDbPassword();
    }

    @Override
    public void connect() throws SQLException {
        if (isConnected()) return;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e1) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e2) {
                plugin.getLog().log(Level.ERROR, "MySQL JDBC driver not found!");
                e2.printStackTrace();
                return;
            }
        }

        String url = "jdbc:mysql:
        connection = DriverManager.getConnection(url, username, password);
    }

    @Override
    public void initialize() {
        String prefix = plugin.getConfigOptions().getDbTablePrefix();
        String tableName = (prefix == null || prefix.isBlank()) ? "vg_alerts" : prefix + "alerts";
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "material VARCHAR(64) NOT NULL," +
                    "count INT NOT NULL," +
                    "world VARCHAR(64) NOT NULL," +
                    "x INT NOT NULL," +
                    "y INT NOT NULL," +
                    "z INT NOT NULL," +
                    "last_x INT," +
                    "last_y INT," +
                    "last_z INT," +
                    "timestamp BIGINT," +
                    "last_timestamp BIGINT" +
                    ")");

            plugin.getLog().log(Level.INFO, "MySQL database initialized.");
        } catch (SQLException e) {
            plugin.getLog().log(Level.ERROR, "Could not initialize MySQL database!");
            e.printStackTrace();
        }
    }

    @Override
    public void saveViolationLevel(UUID uuid, double vl, String tablePrefix) {
        String tableName = (tablePrefix == null || tablePrefix.isBlank()) ? "vg_violations" : tablePrefix + "violations";
        String query = "INSERT INTO " + tableName + " (uuid, violation_level, last_update) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE violation_level = ?, last_update = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            ps.setDouble(2, vl);
            ps.setLong(3, System.currentTimeMillis());
            ps.setDouble(4, vl);
            ps.setLong(5, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
