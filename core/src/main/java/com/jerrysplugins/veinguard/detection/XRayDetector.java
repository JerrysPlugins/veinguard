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

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Experimental detector for direct-to-vein mining patterns (X-Ray).
 */
public class XRayDetector {

    private final VeinTracker veinTracker;
    private final TunnelAnalyzer tunnelAnalyzer;
    private final Map<UUID, MiningPath> playerPaths;

    public XRayDetector(VeinTracker veinTracker) {
        this.veinTracker = veinTracker;
        this.tunnelAnalyzer = new TunnelAnalyzer();
        this.playerPaths = new ConcurrentHashMap<>();
    }

    /**
     * Processes a block break for x-ray pattern detection.
     *
     * @param player    The player breaking the block.
     * @param material  The material of the block.
     * @param location  The location of the block.
     * @param isTracked Whether the material is a tracked ore.
     * @return DetectionResult containing info about the break.
     */
    public DetectionResult processBlockBreak(Player player, Material material, Location location, boolean isTracked) {
        UUID uuid = player.getUniqueId();

        // If it's a non-ore block, we track it as part of the player's current path/tunnel.
        if (!isTracked) {
            playerPaths.computeIfAbsent(uuid, k -> new MiningPath()).addLocation(location);
            return new DetectionResult(false, 0.0, false);
        }

        // If it's a tracked ore block, check if it starts a new vein.
        MiningPath path = playerPaths.get(uuid);
        Vein newVein = veinTracker.processBlockBreak(player, material, location, 
                path != null ? path.getPath() : Collections.emptyList());
        
        if (newVein == null) {
            // It's part of an existing vein being mined, no need to re-analyze path.
            return new DetectionResult(false, 0.0, false);
        }

        // If it's a new vein, analyze the tunnel path leading up to it.
        if (path == null) {
            return new DetectionResult(true, 0.0, newVein.isEncapsulated());
        }

        double suspicionScore = 0.0;

        // Only analyze if the path has some content
        if (path.size() > 0) {
            suspicionScore = tunnelAnalyzer.getDirectnessScore(path, newVein);
            
            // Once a vein is reached, we clear the path to start tracking the tunnel to the next one.
            path.clear();
        }

        return new DetectionResult(true, suspicionScore, newVein.isEncapsulated());
    }

    /**
     * Clears tracking data for a player.
     */
    public void clear(Player player) {
        playerPaths.remove(player.getUniqueId());
    }

    /**
     * Clears all tracking data.
     */
    public void clearAll() {
        playerPaths.clear();
    }
}
