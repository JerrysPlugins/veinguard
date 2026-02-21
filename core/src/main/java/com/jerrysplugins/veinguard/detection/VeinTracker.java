/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.detection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Tracks ore veins mined by players to count an entire vein as a single detection event.
 */
public class VeinTracker {

    // Threshold for considering a mined block as part of a previous vein
    private static final long VEIN_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(1);
    
    // Map of Player UUID -> Set of recently detected Veins
    private final Map<UUID, Set<Vein>> activeVeins = new ConcurrentHashMap<>();

    /**
     * Processes a block break to determine if it belongs to an already tracked vein.
     *
     * @param player The player who broke the block.
     * @param material The material of the block.
     * @param location The location of the broken block.
     * @param ignored Locations to ignore during the encapsulation check.
     * @return The Vein object if this starts a NEW vein, null if it belongs to a vein already in progress.
     */
    public Vein processBlockBreak(Player player, Material material, Location location, Collection<Location> ignored) {
        UUID uuid = player.getUniqueId();
        Set<Vein> playerVeins = activeVeins.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet());

        // 1. Cleanup old veins for this player
        cleanup(playerVeins);

        // 2. Check if the current block belongs to an existing tracked vein
        for (Vein vein : playerVeins) {
            if (vein.getMaterial() == material && vein.contains(location)) {
                // Block is part of an existing vein, don't trigger new alert
                return null;
            }
        }

        // 3. If not found, create a new Vein and scan it
        Vein newVein = new Vein(material, location, ignored);
        playerVeins.add(newVein);

        // This is a NEW vein
        return newVein;
    }

    /**
     * Removes veins from memory that are older than the VEIN_TIMEOUT_MS.
     */
    private void cleanup(Set<Vein> veins) {
        long now = System.currentTimeMillis();
        veins.removeIf(vein -> (now - vein.getFirstBrokenTime()) > VEIN_TIMEOUT_MS);
    }

    /**
     * Clears tracking data for a specific player (e.g. on logout).
     */
    public void clear(Player player) {
        activeVeins.remove(player.getUniqueId());
    }

    /**
     * Clears all tracking data.
     */
    public void clearAll() {
        activeVeins.clear();
    }
}
