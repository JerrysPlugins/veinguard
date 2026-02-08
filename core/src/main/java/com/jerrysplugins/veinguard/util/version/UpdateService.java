/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.version;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateService implements Listener {

    private final VeinGuard plugin;
    private final boolean announceNewVersion;

    private final String latestVersionUrl;

    private volatile String latestVersion;
    private ReleaseChannel latestVersionChannel;

    private final String currentVersion;
    private final ReleaseChannel currentVersionChannel;
    private VersionState currentVersionState = VersionState.UNKNOWN;

    public UpdateService(VeinGuard plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.announceNewVersion = plugin.getVGConfig().getBoolean("show-update-notice", true);

        this.latestVersionUrl = "https://raw.githubusercontent.com/JerrysPlugins/veinguard/refs/heads/master/version.txt";
        this.currentVersion = plugin.getPluginVersion();
        this.currentVersionChannel = ReleaseChannel.fromVersionString(currentVersion);

        retrieveLatestVersion(latest -> {
            if (latest == null) {
                plugin.getLog().log(Level.ERROR, "Update check failed; version state unknown.");
                currentVersionState = VersionState.UNKNOWN;
                return;
            }

            this.latestVersion = latest;
            this.latestVersionChannel = ReleaseChannel.fromVersionString(latestVersion);
            setVersionState(currentVersion, latest);

            logUpdateStatus();
        });
    }

    private void retrieveLatestVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task -> {
            try (InputStream inputStream = new URI(latestVersionUrl).toURL().openStream();
                 Scanner scanner = new Scanner(inputStream)) {

                if (scanner.hasNext()) {
                    consumer.accept(scanner.nextLine().trim());
                } else {
                    consumer.accept(null);
                }

            } catch (IOException | URISyntaxException e) {
                plugin.getLog().log(Level.ERROR, "Unable to retrieve the latest version!");
                consumer.accept(null);
            }
        });
    }

    private int simplifyVersion(String version) {
        if (version == null || version.isBlank()) return 0;

        String numeric = version.split("-")[0];
        String[] parts = numeric.split("\\.");

        int major = parts.length > 0 ? parsePart(parts[0]) : 0;
        int minor = parts.length > 1 ? parsePart(parts[1]) : 0;
        int patch = parts.length > 2 ? parsePart(parts[2]) : 0;

        return (major * 1_000_000) + (minor * 1_000) + patch;
    }

    private int compare(String currentVersion, String latestVersion) {
        int current = simplifyVersion(currentVersion);
        int latest = simplifyVersion(latestVersion);
        return Integer.compare(current, latest);
    }

    private void setVersionState(String currentVersion, String latestVersion) {
        int comparison = compare(currentVersion, latestVersion);

        if (comparison < 0) {
            currentVersionState = VersionState.BEHIND;
        } else if (comparison > 0) {
            currentVersionState = VersionState.AHEAD;
        } else {
            currentVersionState = VersionState.UP_TO_DATE;
        }
    }

    private int parsePart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void logUpdateStatus() {
        switch (currentVersionState) {

            case BEHIND -> plugin.getLog().log(Level.UPDATE, "* New VeinGuard version available! " +
                    "(Current: " + currentVersion + "), " +
                    "(Latest: " + latestVersion + ")");

            case AHEAD -> {
                plugin.getLog().log(Level.NONE, "==============================================================");
                plugin.getLog().log(Level.NONE, "Your version of VeinGuard is "
                        + currentVersionState.getDisplayName() + " of the latest version!");
                plugin.getLog().log(Level.NONE, "(Current: " + currentVersion + ") (Latest: " + latestVersion + ")");
                plugin.getLog().log(Level.NONE, " ");
                plugin.getLog().log(Level.NONE, "Your version is labeled as type (" + currentVersionChannel.getDisplayName() + ")");
                plugin.getLog().log(Level.NONE, "We recommend not running this version on production servers!");
                plugin.getLog().log(Level.NONE, "==============================================================");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!announceNewVersion || currentVersionState != VersionState.BEHIND || latestVersion == null) return;

        Player player = event.getPlayer();
        List<String> newVersionMessage = plugin.getLocale().getListMessage("update", true);

        newVersionMessage.forEach(line ->
                player.sendMessage(
                        line.replace("{oldVersion}", plugin.getPluginVersion())
                                .replace("{newVersion}", latestVersion)
                )
        );
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public ReleaseChannel getCurrentVersionChannel() {
        return currentVersionChannel;
    }

    public ReleaseChannel getLatestVersionChannel() {
        return latestVersionChannel;
    }

    public VersionState getCurrentVersionState() {
        return currentVersionState;
    }
}