/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.detection;

/**
 * Result of an X-Ray detection check.
 */
public class DetectionResult {
    private final boolean newVein;
    private final double score;
    private final boolean encapsulated;

    public DetectionResult(boolean newVein, double score, boolean encapsulated) {
        this.newVein = newVein;
        this.score = score;
        this.encapsulated = encapsulated;
    }

    /**
     * @return True if a new ore vein was started with this block break.
     */
    public boolean isNewVein() {
        return newVein;
    }

    /**
     * @return The directness score of the tunnel leading to this vein.
     */
    public double getScore() {
        return score;
    }

    /**
     * @return True if the vein was completely hidden before discovery.
     */
    public boolean isEncapsulated() {
        return encapsulated;
    }
}
