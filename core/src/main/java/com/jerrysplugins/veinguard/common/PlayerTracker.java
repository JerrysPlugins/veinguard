/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.common;

import com.jerrysplugins.veinguard.VeinGuard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PlayerTracker {

    private final VeinGuard plugin;

    private final ConfigOptions configOptions;

    private final Map<UUID, Map<Material, Deque<Long>>> blockBreakHistory;
    private final Map<UUID, Map<Material, Integer>> incidentCounts;
    private final Map<UUID, Map<Material, Long>> blockAlertCooldowns;
    private final Set<UUID> alertCooldowns;

    private final Map<UUID, Double> violationLevels;

    private final Set<UUID> mutedPlayers;
    private final Set<UUID> mutedStaff;

    private BukkitTask cleanupTask;
    private BukkitTask violationDecayTask;

    public PlayerTracker(VeinGuard plugin) {
        this.plugin = plugin;

        this.configOptions = plugin.getConfigOptions();

        this.blockBreakHistory = new ConcurrentHashMap<>();
        this.incidentCounts = new ConcurrentHashMap<>();
        this.blockAlertCooldowns = new ConcurrentHashMap<>();
        this.alertCooldowns = ConcurrentHashMap.newKeySet();

        this.violationLevels = new ConcurrentHashMap<>();

        this.mutedPlayers = ConcurrentHashMap.newKeySet();
        this.mutedStaff = ConcurrentHashMap.newKeySet();

        scheduleCleanupTaskAsync();
        scheduleViolationDecayTaskAsync();
    }

    public void recordBreak(Player suspect, Material material, Location location) {
        long currentTimeMs = System.currentTimeMillis();
        UUID suspectUUID = suspect.getUniqueId();

        Map<Material, Deque<Long>> suspectHistory =
                blockBreakHistory.computeIfAbsent(suspectUUID, k -> new ConcurrentHashMap<>());

        Deque<Long> timestamps =
                suspectHistory.computeIfAbsent(material, k -> new ConcurrentLinkedDeque<>());

        cleanupOldEntries(timestamps, currentTimeMs);

        if (timestamps.isEmpty()) {

            Map<Material, Integer> pIncidents = incidentCounts.get(suspectUUID);
            if (pIncidents != null) {
                pIncidents.remove(material);
            }
        }

        timestamps.addLast(currentTimeMs);

        incidentCounts.computeIfAbsent(suspectUUID, k -> new ConcurrentHashMap<>())
                .merge(material, 1, Integer::sum);

        int breakThreshold = configOptions.getBreakThreshold(material);
        if (timestamps.size() < breakThreshold) return;

        switch (configOptions.getCooldownType()) {
            case ALERT -> {
                if(isOnAlertCooldown(suspectUUID)) return;
                setPerAlertCooldown(suspectUUID);
            }

            case BLOCK -> {
                if (isOnBlockCooldown(suspectUUID, material, currentTimeMs)) return;
                setPerBlockCooldown(suspectUUID, material, currentTimeMs);
            }
        }

        double oldVl = getViolationLevel(suspectUUID);
        double newVl = incrementViolationLevel(suspectUUID, material);

        int incidentCount = 0;
        Map<Material, Integer> pIncidents = incidentCounts.get(suspectUUID);
        if (pIncidents != null) {
            incidentCount = pIncidents.getOrDefault(material, 0);
        }

        plugin.getAlertManager().sendAlert(suspect, material, location, timestamps.size(), incidentCount, oldVl, newVl);
    }

    private double incrementViolationLevel(UUID uuid, Material material) {
        if (!configOptions.isViolationEnabled()) return 0.0;

        double weight = configOptions.getMaterialWeight(material);
        double initialVl = configOptions.getViolationInitialVl();
        double addedVl = initialVl * weight;

        return violationLevels.merge(uuid, addedVl, Double::sum);
    }

    public double getViolationLevel(UUID uuid) {
        return violationLevels.getOrDefault(uuid, 0.0);
    }

    public void setViolationLevel(UUID uuid, double vl) {
        if (vl <= 0) {
            violationLevels.remove(uuid);
        } else {
            violationLevels.put(uuid, vl);
        }
    }

    public CompletableFuture<Integer> getPlayersInViolationAsync() {
        return CompletableFuture.supplyAsync(() -> {

            int count = 0;
            long currentTimeMs = System.currentTimeMillis();

            for (Map<Material, Deque<Long>> materialMap : blockBreakHistory.values()) {
                for (Map.Entry<Material, Deque<Long>> materialEntry : materialMap.entrySet()) {
                    Material material = materialEntry.getKey();
                    Deque<Long> timestamps = materialEntry.getValue();

                    cleanupOldEntries(timestamps, currentTimeMs);

                    int breakThreshold = configOptions.getBreakThreshold(material);

                    if (timestamps.size() >= breakThreshold) {
                        count++;
                        break;
                    }
                }
            }
            return count;
        });
    }

    public void scheduleCleanupTaskAsync() {
        long delayTicks = 15L * 60L * 20L;

        cleanupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long currentTimeMs = System.currentTimeMillis();
            long expirationTimeMs = configOptions.getCheckIntervalMs();

            for (Iterator<Map.Entry<UUID, Map<Material, Deque<Long>>>> playerIt = blockBreakHistory.entrySet().iterator(); playerIt.hasNext(); ) {
                Map.Entry<UUID, Map<Material, Deque<Long>>> playerEntry = playerIt.next();
                Map<Material, Deque<Long>> materialMap = playerEntry.getValue();

                for (Iterator<Map.Entry<Material, Deque<Long>>> matIt = materialMap.entrySet().iterator(); matIt.hasNext(); ) {
                    Map.Entry<Material, Deque<Long>> matEntry = matIt.next();
                    Deque<Long> deque = matEntry.getValue();

                    while (!deque.isEmpty() && currentTimeMs - deque.peekFirst() > expirationTimeMs) {
                        deque.removeFirst();
                    }

                    if (deque.isEmpty()) {
                        matIt.remove();
                        Map<Material, Integer> pIncidents = incidentCounts.get(playerEntry.getKey());
                        if (pIncidents != null) {
                            pIncidents.remove(matEntry.getKey());
                        }
                    }
                }

                if (materialMap.isEmpty()) {
                    playerIt.remove();
                    incidentCounts.remove(playerEntry.getKey());
                }
            }

            long blockCooldownMs = configOptions.getAlertCooldownMs();
            for (Iterator<Map.Entry<UUID, Map<Material, Long>>> it = blockAlertCooldowns.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<UUID, Map<Material, Long>> entry = it.next();
                Map<Material, Long> cooldowns = entry.getValue();

                cooldowns.entrySet().removeIf(matEntry -> currentTimeMs - matEntry.getValue() >= blockCooldownMs);

                if (cooldowns.isEmpty()) {
                    it.remove();
                }
            }

        }, delayTicks, delayTicks);
    }

    public void scheduleViolationDecayTaskAsync() {
        if (!configOptions.isViolationEnabled()) return;

        long intervalTicks = configOptions.getViolationDecayInterval() * 20L;

        violationDecayTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            double decayAmount = configOptions.getViolationDecayAmount();

            for (Iterator<Map.Entry<UUID, Double>> it = violationLevels.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<UUID, Double> entry = it.next();
                double newVl = entry.getValue() - decayAmount;

                if (newVl <= 0) {
                    it.remove();
                } else {
                    entry.setValue(newVl);
                }
            }
        }, intervalTicks, intervalTicks);
    }

    public void loadViolationLevelAsync(UUID uuid) {
        if (!configOptions.isViolationEnabled()) return;

        CompletableFuture.supplyAsync(() -> plugin.getVGDatabase().loadViolationLevel(uuid, configOptions.getDbTablePrefix()))
                .thenAccept(vl -> {
                    if (vl > 0) {
                        violationLevels.put(uuid, vl);
                    }
                });
    }

    public void saveViolationLevelAsync(UUID uuid) {
        if (!configOptions.isViolationEnabled()) return;

        Double vl = violationLevels.get(uuid);
        if (vl == null) vl = 0.0;

        final double finalVl = vl;
        CompletableFuture.runAsync(() -> plugin.getVGDatabase().saveViolationLevel(uuid, finalVl, configOptions.getDbTablePrefix()));
    }

    private void cleanupOldEntries(Deque<Long> timestamps, long currentTimeMs) {
        while (!timestamps.isEmpty() && currentTimeMs - timestamps.peekFirst() > configOptions.getCheckIntervalMs()) {
            timestamps.removeFirst();
        }
    }

    private boolean isOnBlockCooldown(UUID suspectUUID, Material material, long currentTimeMs) {
        Map<Material, Long> suspectCooldowns = blockAlertCooldowns.get(suspectUUID);
        if (suspectCooldowns == null) return false;

        Long last = suspectCooldowns.get(material);
        if (last == null) return false;

        long elapsed = currentTimeMs - last;
        if (elapsed >= configOptions.getAlertCooldownMs()) {
            suspectCooldowns.remove(material);
            if (suspectCooldowns.isEmpty()) blockAlertCooldowns.remove(suspectUUID);
            return false;
        }

        return true;
    }

    private void setPerBlockCooldown(UUID suspectUUID, Material material, long currentTimeMs) {
        blockAlertCooldowns.computeIfAbsent(suspectUUID, k ->
                new ConcurrentHashMap<>()).put(material, currentTimeMs
        );
    }

    private boolean isOnAlertCooldown(UUID suspectUUID) {
        return alertCooldowns.contains(suspectUUID);
    }

    private void setPerAlertCooldown(UUID suspectUUID) {
        alertCooldowns.add(suspectUUID);
        Bukkit.getScheduler().runTaskLater(plugin, () -> alertCooldowns.remove(suspectUUID),
                configOptions.getAlertCooldownMs() / 50L);
    }

    public void resetPlayerData(Player suspect) {
        UUID uuid = suspect.getUniqueId();
        blockBreakHistory.remove(uuid);
        incidentCounts.remove(uuid);
        blockAlertCooldowns.remove(uuid);
        violationLevels.remove(uuid);
    }

    public void resetAllData() {
        blockBreakHistory.clear();
        incidentCounts.clear();
        blockAlertCooldowns.clear();
        violationLevels.clear();
    }

    public Map<Material, Deque<Long>> getBlockBreakHistory(UUID suspectUUID) {
        return this.blockBreakHistory.get(suspectUUID);
    }

    public void shutdown() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
            cleanupTask = null;
        }
        if (violationDecayTask != null) {
            violationDecayTask.cancel();
            violationDecayTask = null;
        }

        if (configOptions.isViolationEnabled() && !violationLevels.isEmpty()) {
            for (Map.Entry<UUID, Double> entry : violationLevels.entrySet()) {
                plugin.getVGDatabase().saveViolationLevel(entry.getKey(), entry.getValue(), configOptions.getDbTablePrefix());
            }
        }

        blockBreakHistory.clear();
        incidentCounts.clear();
        blockAlertCooldowns.clear();
        violationLevels.clear();
        mutedPlayers.clear();
        mutedStaff.clear();
    }

    public boolean isPlayerTracked(Player suspect) { return suspect.hasPermission("veinguard.bypass"); }

    public void mutePlayer(Player suspect) { mutedPlayers.add(suspect.getUniqueId()); }
    public void unmutePlayer(Player suspect) { mutedPlayers.remove(suspect.getUniqueId()); }
    public boolean isPlayerMuted(Player suspect) { return mutedPlayers.contains(suspect.getUniqueId()); }

    public void muteStaff(Player staff) { mutedStaff.add(staff.getUniqueId()); }
    public void unmuteStaff(Player staff) { mutedStaff.remove(staff.getUniqueId()); }
    public boolean isStaffMuted(Player staff) { return mutedStaff.contains(staff.getUniqueId()); }
}
