/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.detection;

import org.bukkit.Location;

import java.util.List;

/**
 * Analyzes player mining paths to detect direct tunnels to hidden ore veins.
 */
public class TunnelAnalyzer {

    private static final int MIN_PATH_LENGTH = 4;
    private static final double SUSPICIOUS_DIRECTNESS_THRESHOLD = 0.85;

    /**
     * Analyzes a mining path to determine if it's a suspicious direct tunnel to a hidden vein.
     *
     * @param miningPath The sequence of blocks broken leading up to the vein.
     * @param targetVein The vein that was reached at the end of the path.
     * @return A directness score (0.0 to 1.0), where 1.0 is a perfectly straight line.
     */
    public double getDirectnessScore(MiningPath miningPath, Vein targetVein) {
        if (!targetVein.isEncapsulated()) {
            return 0.0;
        }

        List<Location> points = miningPath.getPath();
        if (points.size() < MIN_PATH_LENGTH) {
            return 0.0;
        }

        Location start = points.get(0);
        Location end = points.get(points.size() - 1);

        double dx = Math.abs(end.getX() - start.getX());
        double dy = Math.abs(end.getY() - start.getY());
        double dz = Math.abs(end.getZ() - start.getZ());

        double euclideanDisplacement = start.distance(end);
        double manhattanDisplacement = dx + dy + dz;
        
        // We use the number of blocks as a proxy for the actual distance traveled.
        // Since most players mine in a 1x2 tunnel, we expect roughly 2 blocks per meter of progress.
        // This makes the directness score more robust against the "jumping" between top/bottom blocks
        // that happens when calculating consecutive distances in a path.
        double blocksPerMeter = 2.0;
        double estimatedDistance = points.size() / blocksPerMeter;

        if (estimatedDistance == 0) return 0.0;

        // Euclidean Score: Catches perfectly straight lines (diagonal or horizontal)
        double euclideanScore = euclideanDisplacement / estimatedDistance;
        
        // Manhattan Score: Catches axis-aligned paths like L-shapes and staircases
        double manhattanScore = manhattanDisplacement / estimatedDistance;

        // Take the maximum of the two to get the most "suspicious" interpretation.
        // This hybrid approach ensures that a direct path (even if it turns once) is correctly flagged.
        double finalScore = Math.max(euclideanScore, manhattanScore);

        // Cap the score at 1.0
        return Math.min(1.0, finalScore);
    }

    /**
     * Checks if a path is considered a "direct" path based on a threshold.
     */
    public boolean isDirectPath(MiningPath miningPath, Vein targetVein) {
        return getDirectnessScore(miningPath, targetVein) >= SUSPICIOUS_DIRECTNESS_THRESHOLD;
    }
}
