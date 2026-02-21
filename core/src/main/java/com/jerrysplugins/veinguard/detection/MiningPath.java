/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.detection;

import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;

/**
 * Tracks the path of blocks broken by a player between ore veins.
 */
public class MiningPath {

    private final List<Location> path;
    private static final int MAX_PATH_SIZE = 50;

    public MiningPath() {
        this.path = new LinkedList<>();
    }

    public void addLocation(Location location) {
        if (path.size() >= MAX_PATH_SIZE) {
            path.remove(0);
        }
        path.add(location.clone());
    }

    public List<Location> getPath() {
        return path;
    }

    public void clear() {
        path.clear();
    }

    public int size() {
        return path.size();
    }
}
