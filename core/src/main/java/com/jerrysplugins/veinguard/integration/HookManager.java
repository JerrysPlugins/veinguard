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
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HookManager {

    private final VeinGuard plugin;
    private final Map<String, Hook> registeredHooks;

    public HookManager(VeinGuard plugin) {
        this.plugin = plugin;
        this.registeredHooks = new HashMap<>();
    }

    public void onLoad() {
        if (plugin.getConfigOptions().isWorldGuardEnabled() && Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            addHook(new WorldGuardHook());
        }

        for (Hook hook : registeredHooks.values()) {
            String pluginName = hook.getPluginName();
            if (Bukkit.getPluginManager().getPlugin(pluginName) != null) {
                if (!hook.onLoad()) {
                    plugin.getLog().log(Level.INFO, "Failed to hook into " + pluginName + "!");
                }
            }
        }
    }

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

    public Optional<Hook> getHook(String pluginName) {
        Hook hook = registeredHooks.get(pluginName.toLowerCase());
        if (hook != null && hook.isEnabled()) {
            return Optional.of(hook);
        }
        return Optional.empty();
    }

    public boolean isHookActive(String pluginName) {
        return getHook(pluginName).isPresent();
    }

    public boolean isAllowed(Location location) {
        for (Hook hook : registeredHooks.values()) {
            if (hook.isEnabled() && !hook.isAllowed(location)) {
                return false;
            }
        }
        return true;
    }

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
