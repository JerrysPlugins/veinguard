/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.core.alert;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.ConfigOptions;
import com.jerrysplugins.veinguard.util.VGUtils;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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

    public void sendAlert(Player suspect, Material material, Location location, int count) {

        if (!plugin.getPlayerTracker().isPlayerMuted(suspect)) {

            sendStaffAlerts(suspect, material, count);
            sendConsoleAlert(suspect, material, count);
            sendDiscordAlert(suspect, material, count, location);

            dispatchAlertCommandsAsync(suspect, material, location, count);
        }
    }

    private void sendStaffAlerts(Player suspect, Material material, int count) {

        AlertDeliveryType alertDeliveryType = configOptions.getAlertDeliveryType();

        if(alertDeliveryType == AlertDeliveryType.NONE) return;

        String pluginPrefix = plugin.getLocale().getMessage("plugin-prefix", true);
        String alertMessage = plugin.getLocale().getMessage("staff-notify-chat", true)
                .replace("{player}", suspect.getName())
                .replace("{count}", String.valueOf(count))
                .replace("{material}", configOptions.getPrettyName(material))
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

    private void sendConsoleAlert(Player suspect, Material material, int count) {
        if (!configOptions.isSendAlertConsole()) return;

        String message = plugin.getLocale().getMessage("staff-notify-chat", false)
                .replace("{player}", suspect.getName())
                .replace("{count}", String.valueOf(count))
                .replace("{material}", configOptions.getPrettyName(material))
                .replace("{time}", String.valueOf(configOptions.getCheckIntervalMinutes()));

        plugin.getLog().log(Level.INFO, message);
    }

    private void sendDiscordAlert(Player suspect, Material material, int count, Location location) {
        discordWebhook.sendDiscordWebhookAsync(
                suspect.getName(),
                configOptions.getPrettyName(material),
                count,
                configOptions.getCheckIntervalMinutes(),
                VGUtils.getPrettyLocation(location)
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

    public void dispatchAlertCommandsAsync(Player suspect, Material material, Location location, int count) {
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
                "{z}", String.valueOf(location.getBlockZ())
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