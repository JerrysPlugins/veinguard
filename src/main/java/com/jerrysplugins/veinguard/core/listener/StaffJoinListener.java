package com.jerrysplugins.veinguard.core.listener;

import com.jerrysplugins.veinguard.VeinGuard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class StaffJoinListener implements Listener {

    private final VeinGuard plugin;

    private final String pluginPrefix;

    public StaffJoinListener(VeinGuard plugin) {
        this.plugin = plugin;
        pluginPrefix = plugin.getLocale().getMessage("plugin-prefix", true);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onStaffJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getConfigOptions().isStaffJoinViolationAlert()) return;
        if (!player.hasPermission("veinguard.notify")) return;

        plugin.getPlayerTracker().getPlayersInViolationAsync().thenAccept(count -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (count == 0) return;

                player.sendMessage(pluginPrefix + plugin.getLocale().getMessage("staff-join-notify", true)
                        .replace("{count}", String.valueOf(count)));
            });
        });
    }
}