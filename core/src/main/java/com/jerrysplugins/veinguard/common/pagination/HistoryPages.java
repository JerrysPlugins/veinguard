/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.common.pagination;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.database.DatabaseQueries;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryPages {
    private final VeinGuard plugin;

    public HistoryPages(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public void sendHistoryReport(CommandSender sender, String targetName, long sinceMillis, String timeDisplay, int page) {
        boolean isPlayer = sender instanceof Player;
        List<DatabaseQueries.AlertHistoryEntry> history = plugin.getDatabaseQueries().getPlayerAlertHistory(targetName, sinceMillis);
        int pageSize = plugin.getConfigOptions().getMaxHistoryPageEntries();

        String header = plugin.getLocale().getMessage("history-header", isPlayer)
                .replace("{player}", targetName)
                .replace("{time}", timeDisplay);
        String footerTemplate = plugin.getLocale().getMessage("history-footer", isPlayer);

        if (history.isEmpty()) {
            sender.sendMessage(header);
            sender.sendMessage(plugin.getLocale().getMessage("history-none", isPlayer)
                    .replace("{player}", targetName)
                    .replace("{time}", timeDisplay));
            sender.sendMessage(footerTemplate.replace("{page}", "1").replace("{totalPages}", "1"));
            return;
        }

        PageHandler<DatabaseQueries.AlertHistoryEntry> pageHandler = new PageHandler<>(history, pageSize);

        pageHandler.sendPage(
                page,
                entry -> {
                    long durationSec = (entry.lastTimestamp() - entry.timestamp()) / 1000L;
                    String durationStr = durationSec < 60 ? durationSec + "s" : (durationSec / 60) + "m";

                    String locationStr = String.format("%s, %d, %d, %d", entry.world(), entry.x(), entry.y(), entry.z());
                    if (entry.x() != entry.lastX() || entry.y() != entry.lastY() || entry.z() != entry.lastZ()) {
                        locationStr += String.format(" -> %d, %d, %d", entry.lastX(), entry.lastY(), entry.lastZ());
                    }

                    SimpleDateFormat dateFormat;
                    try {
                        dateFormat = new SimpleDateFormat(plugin.getConfigOptions().getDateFormat());
                    } catch (IllegalArgumentException e) {
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }

                    return plugin.getLocale().getMessage("history-entry", isPlayer)
                            .replace("{time}", dateFormat.format(new Date(entry.timestamp())))
                            .replace("{material}", plugin.getConfigOptions().getPrettyName(entry.material()))
                            .replace("{count}", String.valueOf(entry.count()))
                            .replace("{duration}", durationStr)
                            .replace("{location}", locationStr);
                },
                header,
                footerTemplate.replace("{page}", String.valueOf(page))
                        .replace("{totalPages}", String.valueOf(pageHandler.getTotalPages())),
                (p, line) -> sender.sendMessage(line)
        );
    }

    public int getTotalPages(String targetName, long sinceMillis) {
        List<DatabaseQueries.AlertHistoryEntry> history = plugin.getDatabaseQueries().getPlayerAlertHistory(targetName, sinceMillis);
        int pageSize = plugin.getConfigOptions().getMaxHistoryPageEntries();
        return Math.max(1, (int) Math.ceil(history.size() / (double) pageSize));
    }
}
