package com.jerrysplugins.veinguard.core.common;

import com.jerrysplugins.veinguard.VeinGuard;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTracker {

    private final VeinGuard plugin;

    private final ConfigOptions configOptions;

    private final Map<UUID, Map<Material, Deque<Long>>> blockBreakHistory;
    private final Map<UUID, Map<Material, Long>> alertCooldowns;

    private final List<UUID> mutedPlayers;
    private final List<UUID> mutedStaff;

    public PlayerTracker(VeinGuard plugin) {
        this.plugin = plugin;

        this.configOptions = plugin.getConfigOptions();

        this.blockBreakHistory = new ConcurrentHashMap<>();
        this.alertCooldowns = new ConcurrentHashMap<>();

        this.mutedPlayers = new ArrayList<>();
        this.mutedStaff = new ArrayList<>();
    }

    public void recordBreak(Player suspect, Material material, Location location) {
        long currentTimeMs = System.currentTimeMillis();
        UUID uuid = suspect.getUniqueId();

        Map<Material, Deque<Long>> suspectHistory =
                blockBreakHistory.computeIfAbsent(uuid, k -> new EnumMap<>(Material.class));

        Deque<Long> timestamps =
                suspectHistory.computeIfAbsent(material, k -> new ArrayDeque<>());

        timestamps.addLast(currentTimeMs);
        cleanupOldEntries(timestamps, currentTimeMs);

        int breakThreshold = configOptions.getBreakThreshold(material);
        if (timestamps.size() < breakThreshold) return;

        if (isOnCooldown(uuid, material, currentTimeMs)) return;

        setCooldown(uuid, material, currentTimeMs);

        plugin.getAlertManager().sendAlert(suspect, material, timestamps.size(), location);
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

    private void cleanupOldEntries(Deque<Long> timestamps, long currentTimeMs) {
        while (!timestamps.isEmpty() && currentTimeMs - timestamps.peekFirst() > configOptions.getCheckIntervalMs()) {
            timestamps.removeFirst();
        }
    }

    private boolean isOnCooldown(UUID suspectUUID, Material material, long currentTimeMs) {
        Map<Material, Long> suspectCooldowns = alertCooldowns.get(suspectUUID);
        if (suspectCooldowns == null) return false;
        Long last = suspectCooldowns.get(material);
        return last != null && currentTimeMs - last < configOptions.getAlertCooldownMs();
    }

    private void setCooldown(UUID suspectUUID, Material material, long currentTimeMs) {
        alertCooldowns.computeIfAbsent(suspectUUID, k ->
                new EnumMap<>(Material.class)).put(material, currentTimeMs
        );
    }

    public void resetPlayerData(Player suspect) {
        UUID uuid = suspect.getUniqueId();
        blockBreakHistory.remove(uuid);
        alertCooldowns.remove(uuid);
    }

    public void resetAllData() {
        blockBreakHistory.clear();
        alertCooldowns.clear();
    }

    public Map<Material, Deque<Long>> getBlockBreakHistory(UUID suspectUUID) {
        return this.blockBreakHistory.get(suspectUUID);
    }

    public void shutdown() {
        blockBreakHistory.clear();
        alertCooldowns.clear();
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