/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.common.patrol;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PatrolSession {

    private final UUID staffUuid;
    private final Location originalLocation;
    private final GameMode originalGameMode;
    private final BossBar bossBar;
    
    private final LinkedList<UUID> playersToVisit;
    private final LinkedList<UUID> visitedPlayers;
    private UUID currentPlayer;
    private UUID nextPlayer;
    
    private int secondsRemaining;
    private boolean paused;

    public PatrolSession(Player staff, BossBar bossBar) {
        this.staffUuid = staff.getUniqueId();
        this.originalLocation = staff.getLocation();
        this.originalGameMode = staff.getGameMode();
        this.bossBar = bossBar;
        this.playersToVisit = new LinkedList<>();
        this.visitedPlayers = new LinkedList<>();
        this.paused = false;
        
        this.bossBar.addPlayer(staff);
    }

    public UUID getStaffUuid() {
        return staffUuid;
    }

    public Location getOriginalLocation() {
        return originalLocation;
    }

    public GameMode getOriginalGameMode() {
        return originalGameMode;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public LinkedList<UUID> getPlayersToVisit() {
        return playersToVisit;
    }

    public LinkedList<UUID> getVisitedPlayers() {
        return visitedPlayers;
    }

    public UUID getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(UUID currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public UUID getNextPlayer() {
        return nextPlayer;
    }

    public void setNextPlayer(UUID nextPlayer) {
        this.nextPlayer = nextPlayer;
    }

    public int getSecondsRemaining() {
        return secondsRemaining;
    }

    public void setSecondsRemaining(int secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void refreshPlayerQueue(List<UUID> onlinePlayers) {
        playersToVisit.clear();
        for (UUID uuid : onlinePlayers) {
            if (!uuid.equals(staffUuid) && !uuid.equals(currentPlayer)) {
                playersToVisit.add(uuid);
            }
        }
        Collections.shuffle(playersToVisit);

        // If the queue is empty after filtering the staff member and the current player,
        // it means there's at most one other player online. We should add them back
        // so the patrol can continue.
        if (playersToVisit.isEmpty()) {
            for (UUID uuid : onlinePlayers) {
                if (!uuid.equals(staffUuid)) {
                    playersToVisit.add(uuid);
                }
            }
            Collections.shuffle(playersToVisit);
        }
    }
}
