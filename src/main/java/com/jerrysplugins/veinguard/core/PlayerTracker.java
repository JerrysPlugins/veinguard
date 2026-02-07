/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.core;

import com.jerrysplugins.veinguard.VeinGuard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

public class PlayerTracker {

    private final VeinGuard plugin;

    private final ConfigOptions configOptions;

    private final Map<UUID, Map<Material, Deque<Long>>> blockBreakHistory;
    private final Map<UUID, Map<Material, Long>> blockAlertCooldowns;
    private final Set<UUID> alertCooldowns;

    private final Set<UUID> mutedPlayers;
    private final Set<UUID> mutedStaff;

    public PlayerTracker(VeinGuard plugin) {
        this.plugin = plugin;

        this.configOptions = plugin.getConfigOptions();

        this.blockBreakHistory = new ConcurrentHashMap<>();
        this.blockAlertCooldowns = new ConcurrentHashMap<>();
        this.alertCooldowns = ConcurrentHashMap.newKeySet();

        this.mutedPlayers = new HashSet<>();
        this.mutedStaff = new HashSet<>();

        scheduleCleanupTaskAsync();
    }

    public void recordBreak(Player suspect, Material material, Location location) {
        long currentTimeMs = System.currentTimeMillis();
        UUID suspectUUID = suspect.getUniqueId();

        Map<Material, Deque<Long>> suspectHistory =
                blockBreakHistory.computeIfAbsent(suspectUUID, k -> new EnumMap<>(Material.class));

        Deque<Long> timestamps =
                suspectHistory.computeIfAbsent(material, k -> new ConcurrentLinkedDeque<>());

        timestamps.addLast(currentTimeMs);
        cleanupOldEntries(timestamps, currentTimeMs);

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

        plugin.getAlertManager().sendAlert(suspect, material, location, timestamps.size());
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

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long currentTimeMs = System.currentTimeMillis();
            long expirationTimeMs = configOptions.getCheckIntervalMs() + TimeUnit.MINUTES.toMillis(2);

            for (Map<Material, Deque<Long>> materialMap : blockBreakHistory.values()) {
                for (Deque<Long> deque : materialMap.values()) {
                    while (!deque.isEmpty() && currentTimeMs - deque.peekFirst() > expirationTimeMs) {
                        deque.removeFirst();
                    }
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
                new EnumMap<>(Material.class)).put(material, currentTimeMs
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
        blockAlertCooldowns.remove(uuid);
    }

    public void resetAllData() {
        blockBreakHistory.clear();
        blockAlertCooldowns.clear();
    }

    public Map<Material, Deque<Long>> getBlockBreakHistory(UUID suspectUUID) {
        return this.blockBreakHistory.get(suspectUUID);
    }

    public void shutdown() {
        blockBreakHistory.clear();
        blockAlertCooldowns.clear();
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