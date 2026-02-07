/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.util.update;

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

public class UpdateChecker implements Listener {

    private final VeinGuard plugin;

    private volatile VersionStatus versionStatus = VersionStatus.UP_TO_DATE;
    private final boolean announceUpdate;

    private final String latestVersionUrl = "https://raw.githubusercontent.com/JerrysPlugins/veinguard/refs/heads/master/version.txt";

    private volatile String latestVersion;

    public UpdateChecker(VeinGuard plugin) {
        this.plugin = plugin;
        this.announceUpdate = plugin.getVGConfig().getBoolean("show-update-notice", true);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        retrieveLatestVersion(latest -> {
            this.latestVersion = latest;
            String currentVersion = plugin.getPluginVersion();

            this.versionStatus = VersionComparison.getVersionStatus(currentVersion, latest);
            BuildType buildType = VersionComparison.getBuildType(currentVersion);

            logVersionStatus(currentVersion, latest, buildType);
        });
    }

    public void retrieveLatestVersion(final Consumer<String> consumer) {
        plugin.getLog().log(Level.DEBUG,
                "Pushing method retrieveLatestVersion() in UpdateChecker.class");
        plugin.getLog().log(Level.INFO, "Checking for updates.");

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, task -> {
            try (InputStream inputStream = new URI(latestVersionUrl).toURL().openStream();
                 Scanner scanner = new Scanner(inputStream)) {

                if (scanner.hasNext()) {
                    consumer.accept(scanner.nextLine().trim());
                }

            } catch (IOException | URISyntaxException e) {
                plugin.getLog().log(Level.ERROR,
                        "There was an ERROR while checking for updates!");
                plugin.getLog().log(Level.DEBUG, "Update check error: ", e);
            }
        });
    }

    private void logVersionStatus(String current, String latest, BuildType buildType) {
        switch (versionStatus) {

            case BEHIND -> plugin.getLog().log(Level.UPDATE,
                    "* New version available! (Current: " + current + "), (Latest: " + latest + ")"
            );

            case AHEAD -> {
                plugin.getLog().log(Level.NONE, "==============================================================");
                plugin.getLog().log(Level.NONE, "Your version of VeinGuard is ahead of the latest version!");
                plugin.getLog().log(Level.NONE, "(Your Version: " + current + ")");
                plugin.getLog().log(Level.NONE, "(Latest Version: " + latestVersion + ")");
                plugin.getLog().log(Level.NONE, " ");
                plugin.getLog().log(Level.NONE, "Your version is labeled as type (" + buildType.getDisplayName() + ")");
                plugin.getLog().log(Level.NONE, "We recommend not running this version on production servers!");
                plugin.getLog().log(Level.NONE, "==============================================================");
            }

            case UP_TO_DATE -> plugin.getLog().log(
                    Level.INFO, "You are running the latest version of VeinGuard! (" + latest + ")"
            );
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!announceUpdate || versionStatus != VersionStatus.BEHIND || latestVersion == null) return;

        Player player = event.getPlayer();

        List<String> updateMessage = plugin.getLocale().getListMessage("update", true);

        for (int i = 0; i < updateMessage.size(); i++) {
            String line = updateMessage.get(i);
            line = line.replace("{oldVersion}", plugin.getPluginVersion())
                    .replace("{newVersion}", latestVersion);
            updateMessage.set(i, line);
        }

        if (player.hasPermission("veinguard.update") || player.isOp()) {
            for (String line : updateMessage) {
                player.sendMessage(line);
            }
        }
    }
}