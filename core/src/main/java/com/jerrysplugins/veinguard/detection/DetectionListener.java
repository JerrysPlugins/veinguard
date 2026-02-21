/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.detection;

import com.jerrysplugins.veinguard.VeinGuard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Listener for testing the experimental vein detection system.
 * Sends messages to the player about detected veins and tunnel directness.
 */
public class DetectionListener implements Listener {

    private final VeinGuard plugin;
    private final Map<UUID, Integer> veinCounts = new HashMap<>();
    private static final int CONFIDENCE_START_THRESHOLD = 3;

    public DetectionListener(VeinGuard plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // For testing, only process for players with veinguard.admin or veinguard.mod
        if (!player.hasPermission("veinguard.mod")) return;

        Material material = event.getBlock().getType();
        Location location = event.getBlock().getLocation();
        boolean isTracked = plugin.getConfigOptions().isTrackedMaterial(material);

        DetectionResult result = plugin.getXRayDetector().processBlockBreak(player, material, location, isTracked);

        if (result.isNewVein()) {
            UUID uuid = player.getUniqueId();
            int count = veinCounts.getOrDefault(uuid, 0) + 1;
            veinCounts.put(uuid, count);

            player.sendMessage(ChatColor.AQUA + "[VG-Experimental] " + ChatColor.GREEN + "New vein detected: " + ChatColor.WHITE + material.name());
            player.sendMessage(ChatColor.AQUA + "[VG-Experimental] " + ChatColor.GRAY + "Encapsulated: " + 
                    (result.isEncapsulated() ? ChatColor.GREEN + "YES" : ChatColor.RED + "NO"));

            if (count >= CONFIDENCE_START_THRESHOLD) {
                double score = result.getScore();
                String confidenceColor = getConfidenceColor(score);
                player.sendMessage(ChatColor.AQUA + "[VG-Experimental] " + ChatColor.YELLOW + "Tunnel directness confidence: " + confidenceColor + (int)(score * 100) + "%");
            }
        }
    }

    private String getConfidenceColor(double score) {
        if (score > 0.85) return ChatColor.RED.toString();
        if (score > 0.60) return ChatColor.GOLD.toString();
        return ChatColor.GREEN.toString();
    }
}
