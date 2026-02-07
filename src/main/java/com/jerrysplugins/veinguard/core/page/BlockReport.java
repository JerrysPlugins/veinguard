/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.core.page;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

public class BlockReport {

    private final VeinGuard plugin;

    public BlockReport(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public void sendPlayerReport(Player player, Player suspect, int page) {
        generateReport(suspect, page, true, (p, msg) -> player.sendMessage(msg), null);
    }

    public void sendConsoleReport(Player suspect, int page) {
        generateReport(suspect, page, false, null, (lvl, msg) -> plugin.getLog().log(lvl, msg));
    }

    public int getTotalPages(Player suspect) {
        List<String> reportLines = buildReportMessages(suspect);
        int pageSize = plugin.getConfigOptions().getMaxReportPageEntries();
        return Math.max(1, (int) Math.ceil(reportLines.size() / (double) pageSize));
    }

    private void generateReport(Player suspect, int page, boolean sendToPlayer,
                                BiConsumer<Player, String> playerSender,
                                BiConsumer<Level, String> consoleSender) {

        List<String> reportMessages = buildReportMessages(suspect);
        int pageSize = plugin.getConfigOptions().getMaxReportPageEntries();
        PageHandler<String> pageHandler = new PageHandler<>(reportMessages, pageSize);

        String header = plugin.getLocale().getMessage("report-header", sendToPlayer)
                .replace("{player}", suspect.getName());
        String footerTemplate = plugin.getLocale().getMessage("report-footer", sendToPlayer);

        if (reportMessages.isEmpty()) {
            if (sendToPlayer) playerSender.accept(null, header);
            else consoleSender.accept(Level.NONE, header);

            String none = plugin.getLocale().getMessage("report-none", sendToPlayer)
                    .replace("{time}", String.valueOf(plugin.getConfigOptions().getCheckIntervalMinutes()));
            if (sendToPlayer) playerSender.accept(null, none);
            else consoleSender.accept(Level.NONE, none);

            String footer = footerTemplate.replace("{page}", "1")
                    .replace("{totalPages}", "1");
            if (sendToPlayer) playerSender.accept(null, footer);
            else consoleSender.accept(Level.NONE, footer);

            return;
        }

        pageHandler.sendPage(
                page,
                line -> line,
                header,
                footerTemplate.replace("{page}", String.valueOf(page))
                        .replace("{totalPages}", String.valueOf(pageHandler.getTotalPages())),
                (ignoredPage, line) -> {
                    if (sendToPlayer) playerSender.accept(null, line);
                    else consoleSender.accept(Level.NONE, line);
                }
        );
    }

    private List<String> buildReportMessages(Player suspect) {
        UUID uuid = suspect.getUniqueId();
        Map<Material, Deque<Long>> playerData = plugin.getPlayerTracker().getBlockBreakHistory(uuid);
        List<String> reportMessages = new ArrayList<>();
        long now = System.currentTimeMillis();

        if (playerData != null && !playerData.isEmpty()) {
            for (Map.Entry<Material, Deque<Long>> entry : playerData.entrySet()) {
                Material material = entry.getKey();
                Deque<Long> timestamps = entry.getValue();

                timestamps.removeIf(ts -> now - ts > plugin.getConfigOptions().getCheckIntervalMs());
                int count = timestamps.size();
                if (count == 0) continue;

                int threshold = plugin.getConfigOptions().getBreakThreshold(material);
                boolean isViolation = count >= threshold;

                String messageKey = isViolation ? "report-material-violation" : "report-material-normal";
                String message = plugin.getLocale().getMessage(messageKey, true)
                        .replace("{material}", plugin.getConfigOptions().getPrettyName(material))
                        .replace("{count}", String.valueOf(count))
                        .replace("{threshold}", String.valueOf(threshold));

                reportMessages.add(message);
            }
        }

        return reportMessages;
    }
}