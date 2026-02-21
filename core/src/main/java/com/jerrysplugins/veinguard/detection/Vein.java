/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.detection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;
import java.util.Collection;

/**
 * Represents a single ore vein as a collection of connected blocks.
 */
public class Vein {

    private final UUID id;
    private final Material material;
    private final Set<Location> blocks;
    private final long firstBrokenTime;
    private boolean encapsulated;

    public Vein(Material material, Location startBlock, Collection<Location> ignored) {
        this.id = UUID.randomUUID();
        this.material = material;
        this.blocks = new HashSet<>();
        this.firstBrokenTime = System.currentTimeMillis();
        
        // Initial scan for the whole vein
        scan(startBlock);
        
        // Check if the vein was hidden, ignoring blocks broken by the player
        checkEncapsulation(ignored);
    }

    /**
     * Uses BFS to find all connected blocks of the same material.
     */
    private void scan(Location start) {
        Queue<Block> queue = new LinkedList<>();
        queue.add(start.getBlock());
        blocks.add(start.clone());

        while (!queue.isEmpty()) {
            Block current = queue.poll();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        Block relative = current.getRelative(x, y, z);
                        if (relative.getType() == material && !blocks.contains(relative.getLocation())) {
                            blocks.add(relative.getLocation());
                            queue.add(relative);
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if any block in the vein is touching a non-solid block that is not part of the vein.
     * Note: This is called after the first block of the vein is broken.
     * 
     * @param ignored Locations to ignore during the check (e.g. the player's mining path).
     */
    private void checkEncapsulation(Collection<Location> ignored) {
        for (Location loc : blocks) {
            Block block = loc.getBlock();
            // Check 6 primary faces for exposure to air/non-solid
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        // Only check the 6 direct faces for "exposure"
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) != 1) continue;

                        Block relative = block.getRelative(x, y, z);
                        Location relLoc = relative.getLocation();
                        
                        // A vein is exposed if it touches a non-solid block that is NOT part of the vein itself,
                        // AND NOT part of the player's recent mining path.
                        if (!relative.getType().isSolid() && !blocks.contains(relLoc) && !ignored.contains(relLoc)) {
                            this.encapsulated = false;
                            return;
                        }
                    }
                }
            }
        }
        this.encapsulated = true;
    }

    public UUID getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public Set<Location> getBlocks() {
        return Collections.unmodifiableSet(blocks);
    }

    public boolean contains(Location location) {
        return blocks.contains(location);
    }

    public long getFirstBrokenTime() {
        return firstBrokenTime;
    }

    public boolean isEncapsulated() {
        return encapsulated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vein vein = (Vein) o;
        return Objects.equals(id, vein.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
