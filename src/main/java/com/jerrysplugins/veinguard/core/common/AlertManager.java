package com.jerrysplugins.veinguard.core.common;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.hooks.DiscordWebhook;
import com.jerrysplugins.veinguard.core.util.logger.Level;
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
            String pluginPrefix = plugin.getLocale().getMessage("plugin-prefix", true);
            String notifyMessage = plugin.getLocale().getMessage("staff-notify", true)
                    .replace("{player}", suspect.getName())
                    .replace("{count}", String.valueOf(count))
                    .replace("{material}", plugin.getConfigOptions().getPrettyName(material))
                    .replace("{time}", String.valueOf(plugin.getConfigOptions().getCheckIntervalMinutes()));

            if (plugin.getConfigOptions().isSendAlertsStaff()) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (canReceiveAlerts(onlinePlayer) && !plugin.getPlayerTracker().isStaffMuted(onlinePlayer)) {
                        onlinePlayer.sendMessage(pluginPrefix + notifyMessage);
                    }
                }
            }

            if (plugin.getConfigOptions().isSendAlertsConsole()) {
                plugin.getLog().log(Level.INFO, plugin.getLocale().getMessage("staff-notify", false)
                        .replace("{player}", suspect.getName())
                        .replace("{count}", String.valueOf(count))
                        .replace("{material}", plugin.getConfigOptions().getPrettyName(material))
                        .replace("{time}", String.valueOf(plugin.getConfigOptions().getCheckIntervalMinutes())));
            }

            getDiscordWebhook().sendDiscordWebhookAsync(
                    suspect.getName(),
                    plugin.getConfigOptions().getPrettyName(material),
                    count,
                    plugin.getConfigOptions().getCheckIntervalMinutes(),
                    prettyLocation(location)
            );
        }

        getCommandDispatcher().dispatchAlertCommandsAsync(suspect);
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

    public boolean canReceiveAlerts(Player player) { return player.hasPermission("veinguard.notify"); }

    public CommandDispatcher getCommandDispatcher() { return this.commandDispatcher; }
    public DiscordWebhook getDiscordWebhook() { return this.discordWebhook; }
}