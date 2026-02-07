/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.core;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.alert.AlertDeliveryType;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConfigOptions {

    private final VeinGuard plugin;

    private final Map<Material, Integer> trackedBlocks;
    private final Map<Material, String> prettyNames;

    private final Set<String> disabledWorlds;
    private final Set<Material> ignoredTools;
    private final Set<String> alertCommands;

    private int checkIntervalMinutes;
    private long checkIntervalMs;

    private CooldownType cooldownType;
    private int alertCooldownSeconds;
    private long alertCooldownMs;

    private boolean ignoreCreative;

    private int ignoreAboveY;

    private int maxReportPageEntries;
    private int maxTrackedListPageEntries;

    private boolean sendAlertConsole;
    private AlertDeliveryType alertDeliveryType;

    private boolean staffJoinViolationAlert;

    private boolean alertSoundEnabled;
    private Sound alertSound;
    private float alertSoundVolume;
    private float alertSoundPitch;

    public ConfigOptions(VeinGuard plugin) {
        this.plugin = plugin;
        this.trackedBlocks = new EnumMap<>(Material.class);
        this.prettyNames = new EnumMap<>(Material.class);

        this.disabledWorlds = new HashSet<>();
        this.ignoredTools = new HashSet<>();
        this.alertCommands = new HashSet<>();

        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getVGConfig();

        trackedBlocks.clear();
        prettyNames.clear();

        disabledWorlds.clear();
        ignoredTools.clear();
        alertCommands.clear();

        checkIntervalMinutes = config.getInt("blocks-broken-in-last-minutes", 5);
        checkIntervalMs = checkIntervalMinutes * 60L * 1000L;

        parseCooldownType(config);
        alertCooldownSeconds = config.getInt("alert-cooldown-seconds", 45);
        alertCooldownMs = alertCooldownSeconds * 1000L;

        ignoreCreative = config.getBoolean("ignore-creative-mode", true);

        ignoreAboveY = config.getInt("ignore-above-y-level", 64);

        maxReportPageEntries = config.getInt("player-report-page-entries", 7);
        maxTrackedListPageEntries = config.getInt("tracked-blocks-page-entries", 7);

        sendAlertConsole = config.getBoolean("send-alerts-to-console", true);
        parseAlertDeliveryType(config);

        staffJoinViolationAlert = config.getBoolean("staff-join-violation-alert", false);

        alertSoundEnabled = config.getBoolean("alert-sound.enabled", true);
        alertSoundVolume = (float) config.getDouble("alert-sound.volume", 1.0F);
        alertSoundPitch = (float) config.getDouble("alert-sound.pitch", 1.0F);
        parseAlertSound(config);

        loadIgnoredTools(config);
        loadTrackedBlocks(config);
        loadDisabledWorlds(config);
        loadAlertCommands(config);
    }

    private void loadIgnoredTools(FileConfiguration config) {
        for (String toolEntry : config.getStringList("ignored-tools")) {
            try {
                ignoredTools.add(Material.valueOf(toolEntry.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLog().log(Level.WARN, "Invalid ignored-tool material '" + toolEntry + "' in config.yml! " +
                        "This entry will not be added.");
            }
        }
    }

    private void loadTrackedBlocks(FileConfiguration config) {
        for (String blockEntry : config.getStringList("tracked-blocks")) {
            String[] split = blockEntry.split(":", 3);
            if (split.length < 2) {
                plugin.getLog().log(Level.WARN,
                        "Invalid tracked-block entry '" + blockEntry + "' in config.yml! " +
                                "This entry will not be added.");
                continue;
            }

            Material material;
            try {
                material = Material.valueOf(split[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLog().log(Level.WARN,
                        "Invalid material '" + split[0] + "' in tracked-block entry '" + blockEntry + "' in config.yml! " +
                                "This entry will not be added.");
                continue;
            }

            int threshold;
            try {
                threshold = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                plugin.getLog().log(Level.WARN,
                        "Invalid threshold '" + split[1] + "' in tracked-block entry '" + blockEntry + "' in config.yml! " +
                                "This entry will not be added.");
                continue;
            }

            String prettyName = (split.length == 3 && !split[2].isBlank()) ?
                    split[2].replace("\"", "") : getFallbackMaterialName(material);

            trackedBlocks.put(material, threshold);
            prettyNames.put(material, prettyName);
        }
    }

    private void loadDisabledWorlds(FileConfiguration config) {
        for (String worldEntry : config.getStringList("disabled-worlds")) {
            if (worldEntry == null || worldEntry.isBlank()) continue;
            disabledWorlds.add(worldEntry);
        }
    }

    private void loadAlertCommands(FileConfiguration config) {
        for (String commandEntry : config.getStringList("alert-commands")) {
            if (commandEntry == null || commandEntry.isBlank() || commandEntry.startsWith("examplecmd")) continue;
            alertCommands.add(commandEntry);
        }
    }

    private void parseAlertDeliveryType(FileConfiguration config) {
        String type = config.getString("alert-delivery-type");
        try {
            alertDeliveryType = AlertDeliveryType.valueOf(type);
        } catch (IllegalArgumentException ignored) {
            plugin.getLog().log(Level.WARN, "Invalid alert-delivery type '" + type + "' in config.yml! " +
                    "Defaulting to CHAT.");
            alertDeliveryType = AlertDeliveryType.CHAT;
        }
    }

    private void parseCooldownType(FileConfiguration config) {
        String type = config.getString("alert-cooldown-type");
        try {
            cooldownType = CooldownType.valueOf(type);
        } catch (IllegalArgumentException ignored) {
            plugin.getLog().log(Level.WARN, "Invalid alert-cooldown-type type '" + type + "' in config.yml! " +
                    "Defaulting to PER_BLOCK.");
            cooldownType = CooldownType.BLOCK;
        }
    }

    private void parseAlertSound(FileConfiguration config) {
        String raw = config.getString("alert-sound.sound");

        if (raw == null || raw.trim().isEmpty()) {
            plugin.getLog().log(Level.WARN,
                    "Config option 'alert-sound.sound' is missing or empty in config.yml! Using default sound.");
            alertSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            return;
        }

        try {
            alertSound = Sound.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            plugin.getLog().log(Level.WARN,
                    "Invalid alert sound '" + raw + "' in config.yml! Using default sound.");
            alertSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
    }

    private String getFallbackMaterialName(Material material) {
        String[] words = material.name().toUpperCase().split("_");
        StringBuilder nameBuilder = new StringBuilder();
        for (String word : words) nameBuilder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        return nameBuilder.toString().trim();
    }

    public void addOrUpdateTrackedBlock(Material material, int threshold, String prettyName) {
        trackedBlocks.put(material, threshold);
        prettyNames.put(material, prettyName);

        String configEntry = material.name() + ":" + threshold + ":\"" + prettyName + "\"";

        List<String> list = plugin.getVGConfig().getStringList("tracked-blocks");
        list.removeIf(entry -> entry.startsWith(material.name() + ":"));
        list.add(configEntry);

        plugin.getVGConfig().set("tracked-blocks", list);
        plugin.getConfigFile().saveConfig();
    }

    public void removeTrackedBlock(Material material) {
        trackedBlocks.remove(material);
        prettyNames.remove(material);

        List<String> list = plugin.getVGConfig().getStringList("tracked-blocks");
        list.removeIf(entry -> entry.startsWith(material.name() + ":"));

        plugin.getVGConfig().set("tracked-blocks", list);
        plugin.getConfigFile().saveConfig();
    }

    public void shutdown() {
        trackedBlocks.clear();
        prettyNames.clear();
        disabledWorlds.clear();
        ignoredTools.clear();
        alertCommands.clear();
    }

    public Map<Material, Integer> getTrackedBlocks() { return this.trackedBlocks; }
    public int getBreakThreshold(Material material) {
        return trackedBlocks.getOrDefault(material, 1);
    }
    public boolean isTrackedMaterial(Material material) { return this.trackedBlocks.containsKey(material); }

    public Map<Material, String> getPrettyNames() { return this.prettyNames; }
    public String getPrettyName(Material material) {
        return prettyNames.getOrDefault(material, getFallbackMaterialName(material));
    }

    public boolean isWorldDisabled(World world) { return this.disabledWorlds.contains(world.getName()); }

    public boolean isIgnoredTool(Material material) {
        return ignoredTools.contains(material);
    }

    public Set<String> getAlertCommands() { return this.alertCommands; }

    public int getCheckIntervalMinutes() { return this.checkIntervalMinutes; }
    public long getCheckIntervalMs() { return this.checkIntervalMs; }

    public CooldownType getCooldownType() { return this.cooldownType; }
    public long getAlertCooldownMs() { return this.alertCooldownMs; }

    public boolean isIgnoreCreative() { return this.ignoreCreative; }

    public int getIgnoreAboveY() { return this.ignoreAboveY; }

    public int getMaxReportPageEntries() { return this.maxReportPageEntries; }
    public int getMaxTrackedListPageEntries() { return this.maxTrackedListPageEntries; }

    public boolean isSendAlertConsole() { return this.sendAlertConsole; }
    public AlertDeliveryType getAlertDeliveryType() { return this.alertDeliveryType; }
    public boolean isStaffJoinViolationAlert() { return this.staffJoinViolationAlert; }

    public boolean isAlertSoundEnabled() { return this.alertSoundEnabled; }
    public Sound getAlertSound() { return this.alertSound; }
    public float getAlertSoundVolume() { return this.alertSoundVolume; }
    public float getAlertSoundPitch() { return this.alertSoundPitch; }
}