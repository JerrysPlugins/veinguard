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
import com.jerrysplugins.veinguard.common.pagination.CheckPages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SubCheck implements ISubCommand {

    private final VeinGuard plugin;

    private final CheckPages checkPages;

    public SubCheck(VeinGuard plugin) {
        this.plugin = plugin;
        this.checkPages = new CheckPages(plugin);
    }

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public String getDescription() {
        return "Check a player's block break report.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.check";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "check <player> [page]";
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

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            commandManager.sendMessage(sender, "player-not-found", Map.of("{player}", args[1]));
            return;
        }

        if (plugin.getPlayerTracker().isPlayerTracked(target)) {
            commandManager.sendMessage(sender, "report-not-tracked", Map.of("{player}", target.getName()));
            return;
        }

        int page = 1;

        if (args.length == 3) {
            int totalPages = checkPages.getTotalPages(target);
            try {
                page = Integer.parseInt(args[2]);
                if (page < 1 || page > totalPages) {
                    commandManager.sendMessage(sender, "report-invalid-page", Map.of("{page}", args[2]));
                    return;
                }
            } catch (NumberFormatException ignored) {
                commandManager.sendMessage(sender, "report-invalid-page", Map.of("{page}", args[2]));
                return;
            }
        }

        if (isConsole) {
            checkPages.sendConsoleReport(target, page);
        } else {
            checkPages.sendPlayerReport((Player) sender, target, page);
        }
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
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) return List.of();

            int totalPages = checkPages.getTotalPages(target);

            List<String> pages = new java.util.ArrayList<>();
            pages.add("<Page #>");
            for (int i = 1; i <= totalPages; i++) {
                pages.add(String.valueOf(i));
            }
            return pages;
        }

        return List.of();
    }
}