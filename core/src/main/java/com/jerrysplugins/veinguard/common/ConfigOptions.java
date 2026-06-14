/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.common;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.common.alert.AlertDeliveryType;
import com.jerrysplugins.veinguard.common.patrol.PatrolFinishAction;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConfigOptions {

    private final VeinGuard plugin;

    private final Map<Material, Integer> trackedBlocks;
    private final Map<Material, Double> materialWeights;
    private final Map<Material, String> prettyNames;

    private final Set<String> disabledWorlds;
    private final Set<Material> ignoredTools;
    private final Set<String> alertCommands;

    private final Map<Double, List<String>> violationActions;

    private int checkIntervalMinutes;
    private long checkIntervalMs;

    private CooldownType cooldownType;
    private long alertCooldownMs;

    private boolean ignoreCreative;

    private int ignoreAboveY;

    private boolean violationEnabled;
    private boolean violationActionsEnabled;
    private int violationDecayInterval;
    private double violationDecayAmount;
    private double violationInitialVl;

    private int maxReportPageEntries;
    private int maxTrackedListPageEntries;
    private int maxTopReportPageEntries;
    private int maxHistoryPageEntries;

    private String defaultHistoryTime;
    private String dateFormat;

    private boolean sendAlertConsole;
    private AlertDeliveryType alertDeliveryType;

    private boolean staffJoinViolationAlert;

    private boolean alertSoundEnabled;
    private Sound alertSound;
    private float alertSoundVolume;
    private float alertSoundPitch;

    private int patrolTeleportSeconds;
    private PatrolFinishAction patrolFinishAction;
    private BarColor patrollingBarColor;
    private BarColor patrolPausedBarColor;
    private BarStyle patrolBarStyle;

    private boolean worldGuardEnabled;

    private String databaseType;
    private String dbHost;
    private int dbPort;
    private String dbName;
    private String dbUsername;
    private String dbPassword;
    private String dbTablePrefix;

    private boolean dbCleanupEnabled;
    private int dbCleanupInterval;
    private String dbCleanupRetention;

    public ConfigOptions(VeinGuard plugin) {
        this.plugin = plugin;
        this.trackedBlocks = new EnumMap<>(Material.class);
        this.materialWeights = new EnumMap<>(Material.class);
        this.prettyNames = new EnumMap<>(Material.class);

        this.disabledWorlds = new HashSet<>();
        this.ignoredTools = new HashSet<>();
        this.alertCommands = new HashSet<>();
        this.violationActions = new TreeMap<>(Collections.reverseOrder());

        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getVGConfig();

        trackedBlocks.clear();
        materialWeights.clear();
        prettyNames.clear();

        disabledWorlds.clear();
        ignoredTools.clear();
        alertCommands.clear();
        violationActions.clear();

        checkIntervalMinutes = config.getInt("blocks-broken-in-last-minutes", 5);
        checkIntervalMs = checkIntervalMinutes * 60L * 1000L;

        databaseType = config.getString("database-type", "SQLITE").toUpperCase();
        dbHost = config.getString("mysql-settings.host", "localhost");
        dbPort = config.getInt("mysql-settings.port", 3306);
        dbName = config.getString("mysql-settings.database", "veinguard");
        dbUsername = config.getString("mysql-settings.username", "root");
        dbPassword = config.getString("mysql-settings.password", "");
        dbTablePrefix = config.getString("mysql-settings.table-prefix", "vg_");

        dbCleanupEnabled = config.getBoolean("database-cleanup.enabled", true);
        dbCleanupInterval = config.getInt("database-cleanup.interval", 3600);
        dbCleanupRetention = config.getString("database-cleanup.retention", "30d");

        parseCooldownType(config);
        alertCooldownMs = config.getInt("alert-cooldown-seconds", 45) * 1000L;

        ignoreCreative = config.getBoolean("ignore-creative-mode", true);

        ignoreAboveY = config.getInt("ignore-above-y-level", 64);

        violationEnabled = config.getBoolean("violation-settings.enabled", true);
        violationActionsEnabled = config.getBoolean("violation-settings.actions-enabled", true);
        violationDecayInterval = config.getInt("violation-settings.decay-interval-seconds", 60);
        violationDecayAmount = config.getDouble("violation-settings.decay-amount", 0.5);
        violationInitialVl = config.getDouble("violation-settings.initial-vl-on-alert", 1.0);

        maxReportPageEntries = config.getInt("player-report-page-entries", 7);
        maxTrackedListPageEntries = config.getInt("tracked-blocks-page-entries", 7);
        maxTopReportPageEntries = config.getInt("top-alert-report-page-entries", 7);
        maxHistoryPageEntries = config.getInt("history-report-page-entries", 5);

        defaultHistoryTime = config.getString("history-report-default-time", "1h");
        dateFormat = config.getString("date-format", "yyyy-MM-dd HH:mm:ss");

        sendAlertConsole = config.getBoolean("send-alerts-to-console", true);
        parseAlertDeliveryType(config);

        staffJoinViolationAlert = config.getBoolean("staff-join-violation-alert", false);

        alertSoundEnabled = config.getBoolean("alert-sound.enabled", true);
        alertSoundVolume = (float) config.getDouble("alert-sound.volume", 1.0F);
        alertSoundPitch = (float) config.getDouble("alert-sound.pitch", 1.0F);
        parseAlertSound(config);

        loadIgnoredTools(config);
        loadTrackedBlocks(config);
        loadTrackedBlockMultipliers(config);
        loadDisabledWorlds(config);
        loadAlertCommands(config);
        loadViolationActions(config);

        this.patrolTeleportSeconds = config.getInt("patrol-teleport-seconds", 15);
        parsePatrolFinishAction(config);
        String patrollingBarColorStr = config.getString("patrol-boss-bar.patrolling-color", "BLUE");
        try {
            this.patrollingBarColor = BarColor.valueOf(patrollingBarColorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLog().log(Level.ERROR, "Invalid patrolling boss bar color '" + patrollingBarColorStr + "' in config! Defaulting to BLUE.");
            this.patrollingBarColor = BarColor.BLUE;
        }

        String pausedBarColorStr = config.getString("patrol-boss-bar.paused-color", "YELLOW");
        try {
            this.patrolPausedBarColor = BarColor.valueOf(pausedBarColorStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLog().log(Level.ERROR, "Invalid paused boss bar color '" + pausedBarColorStr + "' in config! Defaulting to YELLOW.");
            this.patrolPausedBarColor = BarColor.YELLOW;
        }

        String barStyleStr = config.getString("patrol-boss-bar.style", "SOLID");
        try {
            this.patrolBarStyle = BarStyle.valueOf(barStyleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLog().log(Level.ERROR, "Invalid boss bar style '" + barStyleStr + "' in config! Defaulting to SOLID.");
            this.patrolBarStyle = BarStyle.SOLID;
        }

        this.worldGuardEnabled = config.getBoolean("enable-worldguard", true);
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
            String[] split = blockEntry.split(":");
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

            String prettyName;
            if (split.length >= 3) {
                prettyName = split[2].replace("\"", "");
            } else {
                prettyName = getFallbackMaterialName(material);
            }

            trackedBlocks.put(material, threshold);
            prettyNames.put(material, prettyName);
        }
    }

    private void loadTrackedBlockMultipliers(FileConfiguration config) {
        if (!config.contains("tracked-blocks-violation-multipliers")) return;

        for (String multiplierEntry : config.getStringList("tracked-blocks-violation-multipliers")) {
            String[] split = multiplierEntry.split(":");
            if (split.length < 2) continue;

            Material material;
            try {
                material = Material.valueOf(split[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                continue;
            }

            try {
                double weight = Double.parseDouble(split[1]);
                materialWeights.put(material, weight);
            } catch (NumberFormatException e) {
                plugin.getLog().log(Level.WARN, "Invalid weight '" + split[1] + "' in tracked-blocks-violation-multipliers for " + split[0]);
            }
        }
    }

    private void loadViolationActions(FileConfiguration config) {
        if (!config.isConfigurationSection("violation-actions")) {
            plugin.getLog().log(Level.DEBUG, "No violation-actions section found in config.");
            return;
        }

        org.bukkit.configuration.ConfigurationSection section = config.getConfigurationSection("violation-actions");
        if (section == null) return;

        Map<String, Object> values = section.getValues(true);
        plugin.getLog().log(Level.DEBUG, "Loading violation actions from config section. Found " + values.size() + " potential keys.");

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String key = entry.getKey();

            if (!(entry.getValue() instanceof List)) continue;

            try {
                double threshold = Double.parseDouble(key);
                List<String> actionsList = new ArrayList<>();

                for (Object obj : (List<?>) entry.getValue()) {
                    actionsList.add(String.valueOf(obj));
                }

                if (!actionsList.isEmpty()) {
                    violationActions.put(threshold, actionsList);
                    plugin.getLog().log(Level.DEBUG, "Successfully loaded " + actionsList.size() + " violation actions for threshold " + threshold);
                }
            } catch (NumberFormatException ignored) {

            }
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

    private void parsePatrolFinishAction(FileConfiguration config) {
        String action = config.getString("patrol-finish-action", "LOOP");
        try {
            this.patrolFinishAction = PatrolFinishAction.valueOf(action.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLog().log(Level.ERROR, "Invalid patrol finish action '" + action + "' in config! Defaulting to LOOP.");
            this.patrolFinishAction = PatrolFinishAction.LOOP;
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
        addOrUpdateTrackedBlock(material, threshold, 1.0, prettyName);
    }

    public void addOrUpdateTrackedBlock(Material material, int threshold, double weight, String prettyName) {
        trackedBlocks.put(material, threshold);
        materialWeights.put(material, weight);
        prettyNames.put(material, prettyName);

        String trackedBlockEntry = material.name() + ":" + threshold + ":\"" + prettyName + "\"";
        List<String> trackedList = plugin.getVGConfig().getStringList("tracked-blocks");
        trackedList.removeIf(entry -> entry.startsWith(material.name() + ":"));
        trackedList.add(trackedBlockEntry);
        plugin.getVGConfig().set("tracked-blocks", trackedList);

        String multiplierEntry = material.name() + ":" + weight;
        List<String> multiplierList = plugin.getVGConfig().getStringList("tracked-blocks-violation-multipliers");
        multiplierList.removeIf(entry -> entry.startsWith(material.name() + ":"));
        multiplierList.add(multiplierEntry);
        plugin.getVGConfig().set("tracked-blocks-violation-multipliers", multiplierList);

        plugin.getConfigFile().saveConfig();
    }

    public void removeTrackedBlock(Material material) {
        trackedBlocks.remove(material);
        materialWeights.remove(material);
        prettyNames.remove(material);

        List<String> trackedList = plugin.getVGConfig().getStringList("tracked-blocks");
        trackedList.removeIf(entry -> entry.startsWith(material.name() + ":"));
        plugin.getVGConfig().set("tracked-blocks", trackedList);

        List<String> multiplierList = plugin.getVGConfig().getStringList("tracked-blocks-violation-multipliers");
        multiplierList.removeIf(entry -> entry.startsWith(material.name() + ":"));
        plugin.getVGConfig().set("tracked-blocks-violation-multipliers", multiplierList);

        plugin.getConfigFile().saveConfig();
    }

    public void shutdown() {
        trackedBlocks.clear();
        materialWeights.clear();
        prettyNames.clear();
        disabledWorlds.clear();
        ignoredTools.clear();
        alertCommands.clear();
        violationActions.clear();
    }

    public Map<Material, Integer> getTrackedBlocks() { return this.trackedBlocks; }
    public int getBreakThreshold(Material material) {
        return trackedBlocks.getOrDefault(material, 1);
    }
    public boolean isTrackedMaterial(Material material) { return this.trackedBlocks.containsKey(material); }

    public Map<Material, Double> getMaterialWeights() { return this.materialWeights; }
    public double getMaterialWeight(Material material) {
        return materialWeights.getOrDefault(material, 1.0);
    }

    public Map<Material, String> getPrettyNames() { return this.prettyNames; }
    public String getPrettyName(Material material) {
        return prettyNames.getOrDefault(material, getFallbackMaterialName(material));
    }

    public String getPrettyName(String materialName) {
        try {
            return getPrettyName(Material.valueOf(materialName.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return materialName;
        }
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

    public boolean isViolationEnabled() { return violationEnabled; }
    public boolean isViolationActionsEnabled() { return violationActionsEnabled; }
    public int getViolationDecayInterval() { return violationDecayInterval; }
    public double getViolationDecayAmount() { return violationDecayAmount; }
    public double getViolationInitialVl() { return violationInitialVl; }
    public Map<Double, List<String>> getViolationActions() { return violationActions; }

    public int getMaxReportPageEntries() { return this.maxReportPageEntries; }
    public int getMaxTrackedListPageEntries() { return this.maxTrackedListPageEntries; }
    public int getMaxTopReportPageEntries() { return this.maxTopReportPageEntries; }
    public int getMaxHistoryPageEntries() { return this.maxHistoryPageEntries; }
    public String getDefaultHistoryTime() { return this.defaultHistoryTime; }
    public String getDateFormat() { return this.dateFormat; }

    public boolean isSendAlertConsole() { return this.sendAlertConsole; }
    public AlertDeliveryType getAlertDeliveryType() { return this.alertDeliveryType; }
    public boolean isStaffJoinViolationAlert() { return this.staffJoinViolationAlert; }

    public boolean isAlertSoundEnabled() { return this.alertSoundEnabled; }
    public Sound getAlertSound() { return this.alertSound; }
    public float getAlertSoundVolume() { return this.alertSoundVolume; }
    public float getAlertSoundPitch() { return this.alertSoundPitch; }

    public int getPatrolTeleportSeconds() { return this.patrolTeleportSeconds; }
    public PatrolFinishAction getPatrolFinishAction() { return this.patrolFinishAction; }
    public BarColor getPatrollingBarColor() { return this.patrollingBarColor; }
    public BarColor getPatrolPausedBarColor() { return this.patrolPausedBarColor; }
    public BarStyle getPatrolBarStyle() { return this.patrolBarStyle; }
    public boolean isWorldGuardEnabled() { return this.worldGuardEnabled; }

    public String getDatabaseType() { return this.databaseType; }
    public String getDbHost() { return this.dbHost; }
    public int getDbPort() { return this.dbPort; }
    public String getDbName() { return this.dbName; }
    public String getDbUsername() { return this.dbUsername; }
    public String getDbPassword() { return this.dbPassword; }
    public String getDbTablePrefix() { return this.dbTablePrefix; }

    public boolean isDbCleanupEnabled() { return this.dbCleanupEnabled; }

    public int getDbCleanupInterval() { return this.dbCleanupInterval; }

    public String getDbCleanupRetention() { return this.dbCleanupRetention; }
}
