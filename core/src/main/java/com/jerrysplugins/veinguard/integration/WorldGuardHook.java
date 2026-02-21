/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.integration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Hook for WorldGuard integration.
 */
public class WorldGuardHook implements Hook {

    public static BooleanFlag VEINGUARD_CHECK;
    private boolean enabled = false;

    @Override
    public String getPluginName() {
        return "WorldGuard";
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void onLoad() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            BooleanFlag flag = new BooleanFlag("veinguard-check");
            registry.register(flag);
            VEINGUARD_CHECK = flag;
        } catch (FlagConflictException e) {
            Flag<?> existing = WorldGuard.getInstance().getFlagRegistry().get("veinguard-check");
            if (existing instanceof BooleanFlag) {
                VEINGUARD_CHECK = (BooleanFlag) existing;
            }
        } catch (NoClassDefFoundError | Exception ignored) {
            // WorldGuard not present or other error
        }
    }

    @Override
    public boolean initialize() {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            try {
                // Check if we can access WorldGuard classes
                WorldGuard.getInstance();
                enabled = true;
                return true;
            } catch (NoClassDefFoundError | Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void shutdown() {
        enabled = false;
    }

    /**
     * Checks if VeinGuard tracking is enabled at the given location based on WorldGuard flags.
     *
     * @param location The location to check.
     * @return True if tracking is enabled (flag is true or not set), false if explicitly disabled (flag is false).
     */
    public boolean isTrackingEnabled(Location location) {
        if (!enabled || VEINGUARD_CHECK == null) return true;

        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            Boolean value = query.queryValue(BukkitAdapter.adapt(location), null, VEINGUARD_CHECK);

            return value == null || value;
        } catch (NoClassDefFoundError | Exception e) {
            return true;
        }
    }

    /**
     * Gets the WorldGuard region container.
     *
     * @return The region container, or null if WorldGuard is not enabled.
     */
    public RegionContainer getRegionContainer() {
        if (!enabled) return null;
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }
}
