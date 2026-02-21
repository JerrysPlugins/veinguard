/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.integration;

/**
 * Represents a hook into another plugin.
 */
public interface Hook {

    /**
     * Gets the name of the plugin this hook is for.
     *
     * @return The plugin name.
     */
    String getPluginName();

    /**
     * Checks if the plugin is currently enabled and the hook is ready.
     *
     * @return True if the hook is active, false otherwise.
     */
    boolean isEnabled();

    /**
     * Called during plugin onLoad.
     * Used for things like registering WorldGuard flags.
     */
    void onLoad();

    /**
     * Attempts to initialize the hook.
     *
     * @return True if successful, false otherwise.
     */
    boolean initialize();

    /**
     * Shuts down the hook and cleans up resources.
     */
    void shutdown();
}
