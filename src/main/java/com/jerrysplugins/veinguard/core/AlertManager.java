package com.jerrysplugins.veinguard.core;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AlertManager {

    private final VeinGuard plugin;
    private final CommandDispatcher commandDispatcher;
    private final DiscordWebhook discordWebhook;

    public AlertManager(VeinGuard plugin) {
        this.plugin = plugin;
        this.commandDispatcher = new CommandDispatcher(plugin);
        this.discordWebhook = new DiscordWebhook(plugin);
    }

    public void sendAlert(Player suspect, Material material, int count, Location location) {
        if (!plugin.getPlayerTracker().isPlayerMuted(suspect)) {
            sendStaffAlerts(suspect, material, count);
            sendConsoleAlert(suspect, material, count);
            sendDiscordAlert(suspect, material, count, location);
        }

        commandDispatcher.dispatchAlertCommandsAsync(suspect);
    }

    private void sendStaffAlerts(Player suspect, Material material, int count) {
        if (!plugin.getConfigOptions().isSendAlertsStaff()) {
            return;
        }

        String prefix = plugin.getLocale().getMessage("plugin-prefix", true);
        String message = plugin.getLocale().getMessage("staff-notify", true)
                .replace("{player}", suspect.getName())
                .replace("{count}", String.valueOf(count))
                .replace("{material}", plugin.getConfigOptions().getPrettyName(material))
                .replace("{time}", String.valueOf(plugin.getConfigOptions().getCheckIntervalMinutes()));

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!canReceiveAlerts(player)) {
                continue;
            }

            if (plugin.getPlayerTracker().isStaffMuted(player)) {
                continue;
            }

            player.sendMessage(prefix + message);

            if (plugin.getConfigOptions().isAlertSoundEnabled()) {
                player.playSound(
                        player.getLocation(),
                        plugin.getConfigOptions().getAlertSound(),
                        plugin.getConfigOptions().getAlertSoundVolume(),
                        plugin.getConfigOptions().getAlertSoundPitch()
                );
            }
        }
    }

    private void sendConsoleAlert(Player suspect, Material material, int count) {
        if (!plugin.getConfigOptions().isSendAlertsConsole()) {
            return;
        }

        String message = plugin.getLocale().getMessage("staff-notify", false)
                .replace("{player}", suspect.getName())
                .replace("{count}", String.valueOf(count))
                .replace("{material}", plugin.getConfigOptions().getPrettyName(material))
                .replace("{time}", String.valueOf(plugin.getConfigOptions().getCheckIntervalMinutes()));

        plugin.getLog().log(Level.INFO, message);
    }

    private void sendDiscordAlert(Player suspect, Material material, int count, Location location) {
        discordWebhook.sendDiscordWebhookAsync(
                suspect.getName(),
                plugin.getConfigOptions().getPrettyName(material),
                count,
                plugin.getConfigOptions().getCheckIntervalMinutes(),
                prettyLocation(location)
        );
    }

    public String prettyLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return "Unknown location.";
        }

        return String.format(
                "(%s) X: %d, Y: %d, Z: %d",
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    public boolean canReceiveAlerts(Player player) {
        return player.hasPermission("veinguard.notify");
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    public DiscordWebhook getDiscordWebhook() {
        return discordWebhook;
    }
}