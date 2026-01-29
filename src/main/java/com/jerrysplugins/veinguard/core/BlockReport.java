package com.jerrysplugins.veinguard.core;

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

    public int getPlayerReportPages(Player suspect) {
        UUID suspectUUID = suspect.getUniqueId();
        Map<Material, Deque<Long>> playerData = plugin.getPlayerTracker().getBlockBreakHistory(suspectUUID);

        if (playerData == null || playerData.isEmpty()) return 1;

        long now = System.currentTimeMillis();
        int validEntries = 0;

        for (Deque<Long> timestamps : playerData.values()) {
            timestamps.removeIf(ts -> now - ts > plugin.getConfigOptions().getCheckIntervalMs());
            if (!timestamps.isEmpty()) validEntries++;
        }

        int pageSize = plugin.getConfigOptions().getMaxReportPageEntries();
        return Math.max(1, (int) Math.ceil(validEntries / (double) pageSize));
    }

    private void generateReport(Player suspect, int page, boolean sendToPlayer, BiConsumer<Player, String> playerSender, BiConsumer<Level, String> consoleSender) {
        UUID targetUUID = suspect.getUniqueId();
        Map<Material, Deque<Long>> playerData = plugin.getPlayerTracker().getBlockBreakHistory(targetUUID);

        long now = System.currentTimeMillis();
        List<String> reportMessages = new ArrayList<>();

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

                String message = plugin.getLocale().getMessage(messageKey, sendToPlayer)
                        .replace("{material}", plugin.getConfigOptions().getPrettyName(material))
                        .replace("{count}", String.valueOf(count))
                        .replace("{threshold}", String.valueOf(threshold));

                reportMessages.add(message);
            }
        }

        int pageSize = plugin.getConfigOptions().getMaxReportPageEntries();
        int totalPages = Math.max(1, (int) Math.ceil(reportMessages.size() / (double) pageSize));
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        String header = plugin.getLocale().getMessage("report-header", sendToPlayer)
                .replace("{player}", suspect.getName());
        if(sendToPlayer) playerSender.accept(null, header); else consoleSender.accept(Level.NONE, header);

        if(reportMessages.isEmpty()) {
            String none = plugin.getLocale().getMessage("report-none", sendToPlayer)
                    .replace("{time}", String.valueOf(plugin.getConfigOptions().getCheckIntervalMinutes()));
            String footer = plugin.getLocale().getMessage("report-footer", sendToPlayer)
                    .replace("{page}", "1")
                    .replace("{totalPages}", "1");

            if(sendToPlayer) {
                playerSender.accept(null, none);
                playerSender.accept(null, footer);
            } else {
                consoleSender.accept(Level.NONE, none);
                consoleSender.accept(Level.NONE, footer);
            }
            return;
        }

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, reportMessages.size());

        for (int i = startIndex; i < endIndex; i++) {
            if(sendToPlayer) playerSender.accept(null, reportMessages.get(i));
            else consoleSender.accept(Level.NONE, reportMessages.get(i));
        }

        String footer = plugin.getLocale().getMessage("report-footer", sendToPlayer)
                .replace("{page}", String.valueOf(page))
                .replace("{totalPages}", String.valueOf(totalPages));
        if(sendToPlayer) playerSender.accept(null, footer);
        else consoleSender.accept(Level.NONE, footer);
    }
}