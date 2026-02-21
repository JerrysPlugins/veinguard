/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.listener;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.integration.Hook;
import com.jerrysplugins.veinguard.integration.WorldGuardHook;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class VGListener implements Listener {

    private final VeinGuard plugin;

    private final String pluginPrefix;

    public VGListener(VeinGuard plugin) {
        this.plugin = plugin;
        pluginPrefix = plugin.getLocale().getMessage("plugin-prefix", true);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("veinguard.bypass")) return;

        Location location = event.getBlock().getLocation();
        World world = player.getWorld();
        Material blockType = event.getBlock().getType();

        if(plugin.getConfigOptions().isWorldDisabled(world)) return;

        // WorldGuard region check
        if (plugin.getHookManager().getHook("WorldGuard").orElse(null) instanceof WorldGuardHook wg) {
            if (!wg.isTrackingEnabled(location)) return;
        }

        if(location.getBlockY() > plugin.getConfigOptions().getIgnoreAboveY()) return;
        if(plugin.getConfigOptions().isIgnoreCreative() && player.getGameMode() == GameMode.CREATIVE) return;
        if(!plugin.getConfigOptions().isTrackedMaterial(blockType)) return;
        if(plugin.getConfigOptions().isIgnoredTool(player.getInventory().getItemInMainHand().getType())) return;

        String bypassMaterial = blockType.name();
        String permissionNode = "veinguard.bypass." + bypassMaterial;
        if (player.hasPermission(permissionNode)) return;

        plugin.getPlayerTracker().recordBreak(player, blockType, location);
    }

    @EventHandler
    public void onStaffJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!plugin.getConfigOptions().isStaffJoinViolationAlert()) return;
        if (!player.hasPermission("veinguard.notify")) return;

        plugin.getPlayerTracker().getPlayersInViolationAsync().thenAccept(count -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (count == 0) return;

            player.sendMessage(pluginPrefix + plugin.getLocale().getMessage("staff-join-notify", true)
                    .replace("{count}", String.valueOf(count)));
        }));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (plugin.getPatrolManager().isPatrolling(player.getUniqueId())) {
            plugin.getPatrolManager().stopPatrol(player, true);
        }
        
        // Clear experimental detection data
        plugin.getVeinTracker().clear(player);
        plugin.getXRayDetector().clear(player);
    }
}