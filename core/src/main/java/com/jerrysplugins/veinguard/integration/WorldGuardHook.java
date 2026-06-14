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
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class WorldGuardHook implements Hook {

    private BooleanFlag veinGuardCheck;
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
    public boolean onLoad() {
        try {
            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            BooleanFlag flag = new BooleanFlag("veinguard-check");
            registry.register(flag);
            veinGuardCheck = flag;
            return true;
        } catch (FlagConflictException e) {
            Flag<?> existing = WorldGuard.getInstance().getFlagRegistry().get("veinguard-check");
            if (existing instanceof BooleanFlag) {
                veinGuardCheck = (BooleanFlag) existing;
            }
            return true;
        } catch (NoClassDefFoundError | Exception ignored) {
            return false;

        }
    }

    @Override
    public boolean initialize() {
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            try {
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

    @Override
    public boolean isAllowed(Location location) {
        if (!enabled || veinGuardCheck == null) return true;

        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            Boolean value = query.queryValue(BukkitAdapter.adapt(location), null, veinGuardCheck);

            return value == null || value;
        } catch (NoClassDefFoundError | Exception e) {
            return true;
        }
    }

    public RegionContainer getRegionContainer() {
        if (!enabled) return null;
        return WorldGuard.getInstance().getPlatform().getRegionContainer();
    }
}
