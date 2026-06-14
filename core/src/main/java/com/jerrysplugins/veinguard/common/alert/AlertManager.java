/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.common.alert;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.api.VeinguardAlertEvent;
import com.jerrysplugins.veinguard.common.ConfigOptions;
import com.jerrysplugins.veinguard.util.VGUtils;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlertManager {

    private final VeinGuard plugin;

    private final ConfigOptions configOptions;

    private final DiscordWebhook discordWebhook;
    private final ActionBarQueue actionBarQueue;

    public AlertManager(VeinGuard plugin) {
        this.plugin = plugin;
        this.configOptions = plugin.getConfigOptions();

        this.discordWebhook = new DiscordWebhook(plugin);
        this.actionBarQueue = new ActionBarQueue(plugin, this);
    }

    public void sendAlert(Player suspect, Material material, Location location, int count, int incidentCount, double oldVl, double newVl) {
        long timestamp = System.currentTimeMillis();

        if (plugin.getPlayerTracker().isPlayerMuted(suspect)) return;

        VeinguardAlertEvent veinguardAlertEvent = new VeinguardAlertEvent(suspect);
        Bukkit.getPluginManager().callEvent(veinguardAlertEvent);
        if (veinguardAlertEvent.isCancelled()) return;

        sendStaffAlerts(suspect, material, count, newVl);
        sendConsoleAlert(suspect, material, count, newVl);
        sendDiscordAlert(suspect, material, count, location, newVl);

        logAlertToDatabaseAsync(suspect, material, location, incidentCount, timestamp);

        dispatchAlertCommandsAsync(suspect, material, location, count, newVl);
        checkViolationActions(suspect, oldVl, newVl);
    }

    private void logAlertToDatabaseAsync(Player suspect, Material material, Location location, int count, long timestamp) {
        if (plugin.getVGDatabase() == null || !plugin.getVGDatabase().isConnected()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String worldName = location.getWorld() != null ? location.getWorld().getName() : "Unknown";
            plugin.getVGDatabase().logAlertToIncident(
                    suspect.getUniqueId(),
                    suspect.getName(),
                    material.name(),
                    count,
                    worldName,
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ(),
                    timestamp,
                    plugin.getConfigOptions().getCheckIntervalMs(),
                    plugin.getConfigOptions().getDbTablePrefix()
            );
        });
    }

    private void sendStaffAlerts(Player suspect, Material material, int count, double vl) {
        AlertDeliveryType alertDeliveryType = configOptions.getAlertDeliveryType();
        if (alertDeliveryType == AlertDeliveryType.NONE) return;

        if (plugin.getPlayerTracker().isPlayerMuted(suspect)) return;

        String pluginPrefix = plugin.getLocale().getMessage("plugin-prefix", true);
        String alertMessage = plugin.getLocale().getMessage("staff-notify-chat", true)
                .replace("{player}", suspect.getName())
                .replace("{count}", String.valueOf(count))
                .replace("{material}", configOptions.getPrettyName(material))
                .replace("{vl}", String.format("%.1f", vl))
                .replace("{time}", String.valueOf(configOptions.getCheckIntervalMinutes()));

        switch (alertDeliveryType) {
            case ACTION_BAR -> getActionBarQueue().queue(pluginPrefix + alertMessage);
            case CHAT -> {
                for (Player staff : Bukkit.getOnlinePlayers()) {
                    if (!canReceiveAlerts(staff)) continue;
                    if (plugin.getPlayerTracker().isStaffMuted(staff)) continue;
                    staff.sendMessage(pluginPrefix + alertMessage);
                    sendAlertSound(staff);
                }
            }
        }
    }

    private void sendConsoleAlert(Player suspect, Material material, int count, double vl) {
        if (!configOptions.isSendAlertConsole()) return;

        String message = plugin.getLocale().getMessage("staff-notify-chat", false)
                .replace("{player}", suspect.getName())
                .replace("{count}", String.valueOf(count))
                .replace("{material}", configOptions.getPrettyName(material))
                .replace("{vl}", String.format("%.1f", vl))
                .replace("{time}", String.valueOf(configOptions.getCheckIntervalMinutes()));

        plugin.getLog().log(Level.INFO, message);
    }

    private void sendDiscordAlert(Player suspect, Material material, int count, Location location, double vl) {
        discordWebhook.sendDiscordWebhookAsync(
                suspect.getName(),
                configOptions.getPrettyName(material),
                count,
                configOptions.getCheckIntervalMinutes(),
                VGUtils.getPrettyLocation(location) + " (VL: " + String.format("%.1f", vl) + ")"
        );
    }

    public void sendAlertSound(Player player) {
        if (!configOptions.isAlertSoundEnabled()) return;
        player.playSound(
                player.getLocation(),
                configOptions.getAlertSound(),
                configOptions.getAlertSoundVolume(),
                configOptions.getAlertSoundPitch()
        );
    }

    public void dispatchAlertCommandsAsync(Player suspect, Material material, Location location, int count, double vl) {
        Set<String> commands = plugin.getConfigOptions().getAlertCommands();
        if (commands == null || commands.isEmpty()) return;

        String worldName = location.getWorld() != null ? location.getWorld().getName() : "Unknown";

        Map<String, String> placeholders = Map.of(
                "{player}", suspect.getName(),
                "{block}", configOptions.getPrettyName(material),
                "{count}", String.valueOf(count),
                "{world}", worldName,
                "{x}", String.valueOf(location.getBlockX()),
                "{y}", String.valueOf(location.getBlockY()),
                "{z}", String.valueOf(location.getBlockZ()),
                "{vl}", String.format("%.1f", vl)
        );

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (String command : commands) {
                if (command == null || command.isBlank()) continue;

                String parsedCommand = VGUtils.applyPlaceholders(command, placeholders);

                Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), parsedCommand)
                );
            }
        });
    }

    private void checkViolationActions(Player suspect, double oldVl, double newVl) {
        if (!configOptions.isViolationEnabled() || !configOptions.isViolationActionsEnabled()) return;

        Map<Double, List<String>> actions = configOptions.getViolationActions();
        if (actions.isEmpty()) return;

        Map<String, String> placeholders = Map.of(
                "{player}", suspect.getName(),
                "{vl}", String.format("%.1f", newVl),
                "{prefix}", plugin.getLocale().getMessage("plugin-prefix", true)
        );

        for (Map.Entry<Double, List<String>> entry : actions.entrySet()) {
            double threshold = entry.getKey();

            if (newVl >= threshold && oldVl < threshold) {
                plugin.getLog().log(Level.DEBUG, "Triggering violation actions for " + suspect.getName() + " at threshold " + threshold);
                for (String command : entry.getValue()) {
                    String parsedCommand = VGUtils.applyPlaceholders(command, placeholders);
                    Bukkit.getScheduler().runTask(plugin, () ->
                        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), parsedCommand));
                }
            }
        }
    }

    public boolean canReceiveAlerts(Player player) {
        return player.hasPermission("veinguard.notify");
    }

    public DiscordWebhook getDiscordWebhook() {
        return discordWebhook;
    }

    public ActionBarQueue getActionBarQueue() {
        return this.actionBarQueue;
    }
}
