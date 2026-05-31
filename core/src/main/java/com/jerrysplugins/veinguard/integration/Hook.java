/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.integration;

import org.bukkit.Location;

public interface Hook {

    String getPluginName();

    boolean isEnabled();

    boolean onLoad();

    boolean initialize();

    void shutdown();

    default boolean isAllowed(Location location) {
        return true;
    }
}