/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.command.subcommand;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.command.CommandManager;
import com.jerrysplugins.veinguard.command.ISubCommand;
import com.jerrysplugins.veinguard.common.pagination.HistoryPages;
import com.jerrysplugins.veinguard.util.VGUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SubHistory implements ISubCommand {

    private final VeinGuard plugin;
    private final HistoryPages historyPages;

    public SubHistory(VeinGuard plugin) {
        this.plugin = plugin;
        this.historyPages = new HistoryPages(plugin);
    }

    @Override
    public String getName() {
        return "history";
    }

    @Override
    public String getDescription() {
        return "Show alert history for a specific player.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.history";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "history <player> [time] [page]";
    }

    @Override
    public boolean showInHelpMessage() {
        return true;
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {
        if (args.length < 2) {
            commandManager.sendUsage(sender, getUsage(), isConsole);
            return;
        }

        String targetName = args[1];
        String timeStr = plugin.getConfigOptions().getDefaultHistoryTime();
        int page = 1;

        if (args.length > 2) {
            long duration = VGUtils.parseTimeStringToMillis(args[2]);
            if (duration > 0) {
                timeStr = args[2];
                if (args.length > 3) {
                    try {
                        page = Integer.parseInt(args[3]);
                    } catch (NumberFormatException e) {
                        commandManager.sendMessage(sender, "history-invalid-page", Map.of("{page}", args[3]));
                        return;
                    }
                }
            } else {
                try {
                    page = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    commandManager.sendMessage(sender, "history-invalid-time", Map.of("{time}", args[2]));
                    return;
                }
            }
        }

        long durationMillis = VGUtils.parseTimeStringToMillis(timeStr);
        long sinceMillis = System.currentTimeMillis() - durationMillis;

        int totalPages = historyPages.getTotalPages(targetName, sinceMillis);
        if (page < 1 || page > totalPages) {
            commandManager.sendMessage(sender, "history-invalid-page", Map.of("{page}", String.valueOf(page)));
            return;
        }

        historyPages.sendHistoryReport(sender, targetName, sinceMillis, timeStr, page);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        if (args.length == 3) {
            return List.of("1h", "2h", "1d", "7d", "30d", "1", "2", "3");
        }
        if (args.length == 4) {
            return List.of("1", "2", "3");
        }
        return List.of();
    }
}
