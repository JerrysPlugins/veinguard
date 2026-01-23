package com.jerrysplugins.veinguard.core.common;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.util.logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigOptions {

    private final VeinGuard plugin;

    private final Map<Material, Integer> trackedBlocks;
    private final Map<Material, String> prettyNames;

    private final Set<String> disabledWorlds;
    private final Set<Material> ignoredTools;
    private final Set<String> alertCommands;

    private int checkIntervalMinutes;
    private long checkIntervalMs;

    private int alertCooldownSeconds;
    private long alertCooldownMs;

    private boolean ignoreCreative;

    private int ignoreAboveY;

    private int maxReportPageEntries;

    private boolean sendAlertsConsole;
    private boolean sendAlertsStaff;
    private boolean staffJoinViolationAlert;

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
        FileConfiguration config = plugin.getConfig();

        trackedBlocks.clear();
        prettyNames.clear();

        disabledWorlds.clear();
        ignoredTools.clear();
        alertCommands.clear();

        checkIntervalMinutes = config.getInt("blocks-broken-in-last-minutes", 5);
        checkIntervalMs = checkIntervalMinutes * 60L * 1000L;

        alertCooldownSeconds = config.getInt("alert-cooldown-seconds", 45);
        alertCooldownMs = alertCooldownSeconds * 1000L;

        ignoreCreative = config.getBoolean("ignore-creative-mode", true);

        ignoreAboveY = config.getInt("ignore-above-y-level", 64);

        maxReportPageEntries = config.getInt("player-report-page-entries", 7);

        sendAlertsConsole = config.getBoolean("send-alerts-to-console", true);
        sendAlertsStaff = config.getBoolean("send-alerts-to-staff", true);
        staffJoinViolationAlert = config.getBoolean("staff-join-violation-alert", false);

        loadIgnoredTools(config);
        loadTrackedBlocks(config);
        loadDisabledWorlds(config);
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

    private String getFallbackMaterialName(Material material) {
        String[] words = material.name().toUpperCase().split("_");
        StringBuilder nameBuilder = new StringBuilder();
        for (String word : words) nameBuilder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        return nameBuilder.toString().trim();
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

    public Set<String> getDisabledWorlds() { return this.disabledWorlds; }
    public boolean isWorldDisabled(World world) { return this.disabledWorlds.contains(world.getName()); }

    public Set<Material> getIgnoredTools() { return this.ignoredTools; }
    public boolean isIgnoredTool(Material material) {
        return ignoredTools.contains(material);
    }

    public Set<String> getAlertCommands() { return this.alertCommands; }

    public int getCheckIntervalMinutes() { return this.checkIntervalMinutes; }
    public long getCheckIntervalMs() { return this.checkIntervalMs; }

    public int getAlertCooldownSeconds() { return this.alertCooldownSeconds; }
    public long getAlertCooldownMs() { return this.alertCooldownMs; }

    public boolean isIgnoreCreative() { return this.ignoreCreative; }

    public int getIgnoreAboveY() { return this.ignoreAboveY; }

    public int getMaxReportPageEntries() { return this.maxReportPageEntries; }

    public boolean isSendAlertsConsole() { return this.sendAlertsConsole; }
    public boolean isSendAlertsStaff() { return this.sendAlertsStaff; }
    public boolean isStaffJoinViolationAlert() { return this.staffJoinViolationAlert; }
}