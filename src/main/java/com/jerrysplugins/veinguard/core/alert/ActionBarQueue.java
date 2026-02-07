/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.core.alert;

import com.jerrysplugins.veinguard.VeinGuard;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActionBarQueue {

    private final VeinGuard plugin;
    private final AlertManager alertManager;

    private final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private BukkitTask task;

    private volatile String activeMessage = null;
    private volatile long messageEndTime = 0L;

    private static final long MESSAGE_DURATION_MS = 5000L;
    private static final long CLIENT_FADE_MS = 1500L;
    private static final long GAP_MS = 1200L;

    private final Set<Player> soundPlayed = ConcurrentHashMap.newKeySet();

    public ActionBarQueue(VeinGuard plugin, AlertManager alertManager) {
        this.plugin = plugin;
        this.alertManager = alertManager;
        startTask();
    }

    public void queue(String message) {
        if (message == null || message.isEmpty()) return;
        messageQueue.add(message);
    }

    private void startTask() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                this::tickAsync,
                1L,
                25L
        );
    }

    private void tickAsync() {
        long now = System.currentTimeMillis();

        if (activeMessage != null && now >= messageEndTime) {
            activeMessage = null;
            soundPlayed.clear();
        }

        if (activeMessage == null && !messageQueue.isEmpty()) {
            long allowedStart = messageEndTime + CLIENT_FADE_MS + GAP_MS;
            if (now >= allowedStart) {
                activeMessage = messageQueue.poll();
                messageEndTime = now + MESSAGE_DURATION_MS;
                soundPlayed.clear();
            }
        }

        if (activeMessage == null) return;
        sendActionBarMessage(activeMessage);
    }

    private void sendActionBarMessage(String message) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player staff : Bukkit.getOnlinePlayers()) {
                if (!alertManager.canReceiveAlerts(staff) || plugin.getPlayerTracker().isStaffMuted(staff)) continue;

                staff.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText(message)
                );

                if (!soundPlayed.contains(staff)) {
                    alertManager.sendAlertSound(staff);
                    soundPlayed.add(staff);
                }
            }
        });
    }

    public void shutdown() {
        if (task != null) task.cancel();
        messageQueue.clear();
        activeMessage = null;
        soundPlayed.clear();
    }
}