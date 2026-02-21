/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.common.pagination;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.command.ISubCommand;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiConsumer;

public class HelpPages {

    private static final int PAGE_SIZE = 7;

    private final VeinGuard plugin;

    public HelpPages(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public void sendPlayerHelp(Player player, int page) {
        generateHelp(page, (p, msg) -> player.sendMessage(msg), null, true);
    }

    public void sendConsoleHelp(int page) {
        generateHelp(page, null, (lvl, msg) -> plugin.getLog().log(lvl, msg), false);
    }

    public int getTotalPages() {
        long size = plugin.getCommandManager().getSubCommands().values().stream()
                .filter(ISubCommand::showInHelpMessage)
                .count();

        return Math.max(1, (int) Math.ceil(size / (double) PAGE_SIZE));
    }

    private void generateHelp(int page,
                              BiConsumer<Player, String> playerSender,
                              BiConsumer<Level, String> consoleSender,
                              boolean sendToPlayer) {

        Map<String, ISubCommand> subCommands = plugin.getCommandManager().getSubCommands();

        List<ISubCommand> sortedSubs = subCommands.values().stream()
                .filter(ISubCommand::showInHelpMessage)
                .sorted(Comparator.comparing(sub -> sub.getName().toLowerCase(Locale.ROOT)))
                .toList();

        List<String> messages = new ArrayList<>(sortedSubs.size());

        for (ISubCommand sub : sortedSubs) {
            String entry = plugin.getLocale().getMessage("help-list-entry", sendToPlayer)
                    .replace("{subCommand}", sub.getName())
                    .replace("{usage}", sub.getUsage())
                    .replace("{description}", sub.getDescription());

            messages.add(entry);
        }

        PageHandler<String> pageHandler = new PageHandler<>(messages, PAGE_SIZE);

        String header = plugin.getLocale().getMessage("help-list-header", sendToPlayer);

        String footer = plugin.getLocale().getMessage("help-list-footer", sendToPlayer)
                .replace("{page}", String.valueOf(page))
                .replace("{totalPages}", String.valueOf(pageHandler.getTotalPages()));

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
