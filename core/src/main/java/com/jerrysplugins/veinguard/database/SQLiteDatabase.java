/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.database;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class SQLiteDatabase extends Database {

    private final VeinGuard plugin;
    private final File dbFile;

    public SQLiteDatabase(VeinGuard plugin) {
        this.plugin = plugin;
        this.dbFile = new File(plugin.getDataFolder(), "statistics.db");
    }

    @Override
    public void connect() throws SQLException {
        if (isConnected()) return;

        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                plugin.getLog().log(Level.ERROR, "Could not create SQLite database file!");
                e.printStackTrace();
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        } catch (ClassNotFoundException e) {
            plugin.getLog().log(Level.ERROR, "SQLite JDBC driver not found!");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize() {
        String prefix = plugin.getConfigOptions().getDbTablePrefix();
        String alertsTable = (prefix == null || prefix.isBlank()) ? "vg_alerts" : prefix + "alerts";
        String violationsTable = (prefix == null || prefix.isBlank()) ? "vg_violations" : prefix + "violations";

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS " + alertsTable + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "uuid TEXT NOT NULL," +
                    "player_name TEXT NOT NULL," +
                    "material TEXT NOT NULL," +
                    "count INTEGER NOT NULL," +
                    "world TEXT NOT NULL," +
                    "x INTEGER NOT NULL," +
                    "y INTEGER NOT NULL," +
                    "z INTEGER NOT NULL," +
                    "last_x INTEGER," +
                    "last_y INTEGER," +
                    "last_z INTEGER," +
                    "timestamp BIGINT," +
                    "last_timestamp BIGINT" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + violationsTable + " (" +
                    "uuid TEXT PRIMARY KEY," +
                    "violation_level REAL NOT NULL," +
                    "last_update BIGINT NOT NULL" +
                    ")");

            plugin.getLog().log(Level.INFO, "SQLite database initialized.");
        } catch (SQLException e) {
            plugin.getLog().log(Level.ERROR, "Could not initialize SQLite database!");
            e.printStackTrace();
        }
    }

    @Override
    public void saveViolationLevel(UUID uuid, double vl, String tablePrefix) {
        String tableName = (tablePrefix == null || tablePrefix.isBlank()) ? "vg_violations" : tablePrefix + "violations";
        String query = "INSERT INTO " + tableName + " (uuid, violation_level, last_update) VALUES (?, ?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET violation_level = excluded.violation_level, last_update = excluded.last_update";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, uuid.toString());
            ps.setDouble(2, vl);
            ps.setLong(3, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
