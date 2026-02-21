/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.common.patrol;

import com.jerrysplugins.veinguard.VeinGuard;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PatrolManager {

    private final VeinGuard plugin;
    private final Map<UUID, PatrolSession> activeSessions;
    private BukkitTask patrolTask;

    public PatrolManager(VeinGuard plugin) {
        this.plugin = plugin;
        this.activeSessions = new HashMap<>();
        startPatrolTask();
    }

    public void startPatrol(Player staff) {
        if (activeSessions.containsKey(staff.getUniqueId())) {
            sendMessage(staff, "patrol-already-started");
            return;
        }

        ArrayList<UUID> players = (ArrayList<UUID>) Bukkit.getOnlinePlayers().stream()
                .map(Player::getUniqueId)
                .filter(uuid -> !uuid.equals(staff.getUniqueId()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (players.isEmpty()) {
            sendMessage(staff, "patrol-no-players");
            return;
        }

        PatrolSession session = new PatrolSession(staff, Bukkit.createBossBar(
                "",
                plugin.getConfigOptions().getPatrollingBarColor(),
                plugin.getConfigOptions().getPatrolBarStyle()
        ));

        session.refreshPlayerQueue(players);
        activeSessions.put(staff.getUniqueId(), session);

        staff.setGameMode(GameMode.SPECTATOR);
        sendMessage(staff, "patrol-start");

        teleportToNext(session);
    }

    public void stopPatrol(Player staff) {
        stopPatrol(staff, false);
    }

    public void stopPatrol(Player staff, boolean silent) {
        PatrolSession session = activeSessions.remove(staff.getUniqueId());
        if (session == null) {
            if (!silent) sendMessage(staff, "patrol-not-started");
            return;
        }

        session.getBossBar().removeAll();
        staff.setGameMode(session.getOriginalGameMode());
        staff.teleport(session.getOriginalLocation());
        if (!silent) sendMessage(staff, "patrol-stop");
    }

    public void pausePatrol(Player staff) {
        PatrolSession session = activeSessions.get(staff.getUniqueId());
        if (session == null) {
            sendMessage(staff, "patrol-not-started");
            return;
        }

        if (session.isPaused()) return;

        session.setPaused(true);
        updateBossBar(session);
        sendMessage(staff, "patrol-pause");
    }

    public void resumePatrol(Player staff) {
        PatrolSession session = activeSessions.get(staff.getUniqueId());
        if (session == null) {
            sendMessage(staff, "patrol-not-started");
            return;
        }

        if (!session.isPaused()) return;

        session.setPaused(false);
        updateBossBar(session);
        sendMessage(staff, "patrol-resume");
    }

    public void nextPatrol(Player staff) {
        PatrolSession session = activeSessions.get(staff.getUniqueId());
        if (session == null) {
            sendMessage(staff, "patrol-not-started");
            return;
        }
        teleportToNext(session);
        sendMessage(staff, "patrol-next");
    }

    public void backPatrol(Player staff) {
        PatrolSession session = activeSessions.get(staff.getUniqueId());
        if (session == null) {
            sendMessage(staff, "patrol-not-started");
            return;
        }

        if (session.getVisitedPlayers().isEmpty()) {
            sendMessage(staff, "patrol-no-previous-player");
            return;
        }

        UUID prevUuid = session.getVisitedPlayers().pollFirst();
        Player target = Bukkit.getPlayer(prevUuid);

        if (target == null || !target.isOnline()) {
            // If the previous player is offline, try to go back further
            backPatrol(staff);
            return;
        }

        if (session.getCurrentPlayer() != null) {
            session.getPlayersToVisit().addFirst(session.getCurrentPlayer());
        }

        session.setCurrentPlayer(prevUuid);
        session.setNextPlayer(session.getPlayersToVisit().peek());

        staff.teleport(target.getLocation());
        session.setSecondsRemaining(plugin.getConfigOptions().getPatrolTeleportSeconds());
        updateBossBar(session);
        sendMessage(staff, "patrol-back");
    }

    public boolean isPatrolling(UUID staffUuid) {
        return activeSessions.containsKey(staffUuid);
    }

    private void startPatrolTask() {
        patrolTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (PatrolSession session : new ArrayList<>(activeSessions.values())) {
                if (session.isPaused()) continue;

                Player staff = Bukkit.getPlayer(session.getStaffUuid());
                if (staff == null) {
                    activeSessions.remove(session.getStaffUuid());
                    session.getBossBar().removeAll();
                    continue;
                }

                session.setSecondsRemaining(session.getSecondsRemaining() - 1);
                if (session.getSecondsRemaining() <= 0) {
                    teleportToNext(session);
                } else {
                    updateBossBar(session);
                }
            }
        }, 20L, 20L);
    }

    private void teleportToNext(PatrolSession session) {
        Player staff = Bukkit.getPlayer(session.getStaffUuid());
        if (staff == null) return;

        if (session.getCurrentPlayer() != null) {
            session.getVisitedPlayers().addFirst(session.getCurrentPlayer());
            if (session.getVisitedPlayers().size() > 20) {
                session.getVisitedPlayers().removeLast();
            }
        }

        if (session.getPlayersToVisit().isEmpty()) {
            if (plugin.getConfigOptions().getPatrolFinishAction() == PatrolFinishAction.STOP) {
                stopPatrol(staff, true);
                sendMessage(staff, "patrol-finished");
                return;
            }

            session.refreshPlayerQueue(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getUniqueId)
                    .collect(Collectors.toList()));
        }

        if (session.getPlayersToVisit().isEmpty()) {
            stopPatrol(staff);
            return;
        }

        UUID nextUuid = session.getPlayersToVisit().poll();
        Player target = Bukkit.getPlayer(nextUuid);

        if (target == null || !target.isOnline()) {
            teleportToNext(session);
            return;
        }

        session.setCurrentPlayer(nextUuid);
        
        UUID peekNext = session.getPlayersToVisit().peek();
        session.setNextPlayer(peekNext);

        staff.teleport(target.getLocation());
        session.setSecondsRemaining(plugin.getConfigOptions().getPatrolTeleportSeconds());
        updateBossBar(session);
    }

    private void updateBossBar(PatrolSession session) {
        String currentName = Bukkit.getOfflinePlayer(session.getCurrentPlayer()).getName();
        if (currentName == null) currentName = "Unknown";

        if (session.isPaused()) {
            session.getBossBar().setColor(plugin.getConfigOptions().getPatrolPausedBarColor());
            session.getBossBar().setTitle(plugin.getLocale().getMessage("patrol-boss-bar-paused", true)
                    .replace("{current}", currentName));
            session.getBossBar().setProgress(1.0);
        } else {
            session.getBossBar().setColor(plugin.getConfigOptions().getPatrollingBarColor());
            String nextName = "None";
            if (session.getNextPlayer() != null) {
                nextName = Bukkit.getOfflinePlayer(session.getNextPlayer()).getName();
                if (nextName == null) nextName = "Unknown";
            }

            session.getBossBar().setTitle(plugin.getLocale().getMessage("patrol-boss-bar-title", true)
                    .replace("{current}", currentName)
                    .replace("{next}", nextName)
                    .replace("{timer}", String.valueOf(session.getSecondsRemaining())));

            double progress = (double) session.getSecondsRemaining() / plugin.getConfigOptions().getPatrolTeleportSeconds();
            session.getBossBar().setProgress(Math.max(0.0, Math.min(1.0, progress)));
        }
    }

    public void shutdown() {
        if (patrolTask != null) {
            patrolTask.cancel();
        }
        for (PatrolSession session : activeSessions.values()) {
            Player staff = Bukkit.getPlayer(session.getStaffUuid());
            if (staff != null) {
                staff.setGameMode(session.getOriginalGameMode());
                staff.teleport(session.getOriginalLocation());
            }
            session.getBossBar().removeAll();
        }
        activeSessions.clear();
    }

    private void sendMessage(Player player, String key) {
        String prefix = plugin.getLocale().getMessage("plugin-prefix", true);
        String message = plugin.getLocale().getMessage(key, true);
        player.sendMessage(prefix + message);
    }
}