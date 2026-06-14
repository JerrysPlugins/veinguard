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

import java.util.List;

public class TopPlayerPages {

    private final VeinGuard plugin;

    public TopPlayerPages(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public void sendTopReport(CommandSender sender, long sinceMillis, String timeDisplay, int page) {
        boolean isPlayer = sender instanceof Player;
        generateReport(sender, sinceMillis, timeDisplay, page, isPlayer);
    }

    public int getTotalPages(long sinceMillis) {
        List<DatabaseQueries.TopEntry> topPlayers = plugin.getDatabaseQueries().getTopPlayers(sinceMillis);
        int pageSize = plugin.getConfigOptions().getMaxTopReportPageEntries();
        return Math.max(1, (int) Math.ceil(topPlayers.size() / (double) pageSize));
    }

    private void generateReport(CommandSender sender, long sinceMillis, String timeDisplay, int page, boolean isPlayer) {
        List<DatabaseQueries.TopEntry> topPlayers = plugin.getDatabaseQueries().getTopPlayers(sinceMillis);
        int pageSize = plugin.getConfigOptions().getMaxTopReportPageEntries();

        String header = plugin.getLocale().getMessage("top-header", isPlayer)
                .replace("{time}", timeDisplay);
        String footerTemplate = plugin.getLocale().getMessage("top-footer", isPlayer);

        if (topPlayers.isEmpty()) {
            sender.sendMessage(header);
            sender.sendMessage(plugin.getLocale().getMessage("top-none", isPlayer));
            sender.sendMessage(footerTemplate.replace("{page}", "1").replace("{totalPages}", "1"));
            return;
        }

        PageHandler<DatabaseQueries.TopEntry> pageHandler = new PageHandler<>(topPlayers, pageSize);

        pageHandler.sendPage(
                page,
                entry -> plugin.getLocale().getMessage("top-entry", isPlayer)
                        .replace("{player}", entry.playerName())
                        .replace("{count}", String.valueOf(entry.totalBlocks())),
                header,
                footerTemplate.replace("{page}", String.valueOf(page))
                        .replace("{totalPages}", String.valueOf(pageHandler.getTotalPages())),
                (p, line) -> sender.sendMessage(line)
        );
    }
}
