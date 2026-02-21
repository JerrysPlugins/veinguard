/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.integration;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages hooks into external plugins.
 */
public class HookManager {

    private final VeinGuard plugin;
    private final Map<String, Hook> registeredHooks;

    public HookManager(VeinGuard plugin) {
        this.plugin = plugin;
        this.registeredHooks = new HashMap<>();
    }

    /**
     * Called during plugin onLoad.
     */
    public void onLoad() {
        // Register all built-in hooks
        if (plugin.getConfigOptions().isWorldGuardEnabled()) {
            addHook(new WorldGuardHook());
        }

        // Call onLoad for each hook
        for (Hook hook : registeredHooks.values()) {
            try {
                hook.onLoad();
            } catch (Exception e) {
                plugin.getLog().log(Level.ERROR, "Error during onLoad for hook " + hook.getPluginName(), e);
            }
        }
    }

    /**
     * Called during plugin onEnable.
     */
    public void onEnable() {
        for (Hook hook : registeredHooks.values()) {
            String pluginName = hook.getPluginName();
            if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
                if (hook.initialize()) {
                    plugin.getLog().log(Level.INFO, "Successfully hooked into " + pluginName);
                } else {
                    plugin.getLog().log(Level.WARN, "Failed to initialize hook for " + pluginName);
                }
            }
        }
    }

    /**
     * Registers a new hook. If the plugin is already enabled, it will be initialized immediately.
     *
     * @param hook The hook to register.
     */
    public void registerHook(Hook hook) {
        addHook(hook);
        String pluginName = hook.getPluginName();
        if (Bukkit.getPluginManager().isPluginEnabled(pluginName)) {
            if (hook.initialize()) {
                plugin.getLog().log(Level.INFO, "Successfully hooked into " + pluginName);
            }
        }
    }

    private void addHook(Hook hook) {
        registeredHooks.put(hook.getPluginName().toLowerCase(), hook);
    }

    /**
     * Gets a hook by plugin name.
     *
     * @param pluginName The name of the plugin.
     * @return An Optional containing the hook if found and enabled.
     */
    public Optional<Hook> getHook(String pluginName) {
        Hook hook = registeredHooks.get(pluginName.toLowerCase());
        if (hook != null && hook.isEnabled()) {
            return Optional.of(hook);
        }
        return Optional.empty();
    }

    /**
     * Checks if a hook is registered and enabled for a given plugin.
     *
     * @param pluginName The name of the plugin.
     * @return True if a hook is active.
     */
    public boolean isHookActive(String pluginName) {
        return getHook(pluginName).isPresent();
    }

    /**
     * Shuts down all registered hooks.
     */
    public void shutdown() {
        for (Hook hook : registeredHooks.values()) {
            try {
                hook.shutdown();
            } catch (Exception e) {
                plugin.getLog().log(Level.ERROR, "Error shutting down hook for " + hook.getPluginName(), e);
            }
        }
        registeredHooks.clear();
    }
}
