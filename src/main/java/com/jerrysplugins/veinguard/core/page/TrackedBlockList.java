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

public class TrackedBlockList {

    private final VeinGuard plugin;

    public TrackedBlockList(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public void sendPlayerList(Player player, int page) {
        generateList(page, (p, msg) -> player.sendMessage(msg), null, true);
    }

    public void sendConsoleList(int page) {
        generateList(page, null, (lvl, msg) -> plugin.getLog().log(lvl, msg), false);
    }

    public int getTotalPages() {
        int pageSize = plugin.getConfigOptions().getMaxTrackedListPageEntries();
        return Math.max(1, (int) Math.ceil(plugin.getConfigOptions().getTrackedBlocks().size() / (double) pageSize));
    }

    private void generateList(int page,
                              BiConsumer<Player, String> playerSender,
                              BiConsumer<Level, String> consoleSender,
                              boolean sendToPlayer) {

        Map<Material, String> prettyNames = plugin.getConfigOptions().getPrettyNames();
        Map<Material, Integer> trackedBlocks = plugin.getConfigOptions().getTrackedBlocks();

        List<String> messages = new ArrayList<>();
        int entryNumber = 1;
        for (Material material : trackedBlocks.keySet()) {
            int threshold = trackedBlocks.get(material);
            String prettyName = prettyNames.getOrDefault(material, material.name());

            String entryMessage = plugin.getLocale().getMessage("tracked-blocks-list-entry", sendToPlayer)
                    .replace("{numEntry}", String.valueOf(entryNumber))
                    .replace("{material}", material.name())
                    .replace("{threshold}", String.valueOf(threshold))
                    .replace("{prettyName}", prettyName);

            messages.add(entryMessage);
            entryNumber++;
        }

        int pageSize = plugin.getConfigOptions().getMaxTrackedListPageEntries();
        PageHandler<String> pageHandler = new PageHandler<>(messages, pageSize);

        String header = plugin.getLocale().getMessage("tracked-blocks-list-header", sendToPlayer)
                .replace("{page}", String.valueOf(page))
                .replace("{totalPages}", String.valueOf(pageHandler.getTotalPages()));

        String footer = plugin.getLocale().getMessage("tracked-blocks-list-footer", sendToPlayer)
                .replace("{page}", String.valueOf(page))
                .replace("{totalPages}", String.valueOf(pageHandler.getTotalPages()));

        if (messages.isEmpty()) {
            if (sendToPlayer) playerSender.accept(null, header);
            else consoleSender.accept(Level.NONE, header);

            String none = plugin.getLocale().getMessage("tracked-blocks-list-none", sendToPlayer);
            if (sendToPlayer) playerSender.accept(null, none);
            else consoleSender.accept(Level.NONE, none);

            if (sendToPlayer) playerSender.accept(null, footer);
            else consoleSender.accept(Level.NONE, footer);
        } else {
            pageHandler.sendPage(
                    page,
                    line -> line,
                    header,
                    footer,
                    (ignoredPage, line) -> {
                        if (sendToPlayer) playerSender.accept(null, line);
                        else consoleSender.accept(Level.NONE, line);
                    }
            );
        }
    }
}