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
import com.jerrysplugins.veinguard.common.pagination.HelpPages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SubHelp implements ISubCommand {

    private final VeinGuard plugin;

    private final HelpPages helpPages;

    public SubHelp(VeinGuard plugin) {
        this.plugin = plugin;
        this.helpPages = new HelpPages(plugin);
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "View the command help message.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.help";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "help [page]";
    }

    @Override
    public boolean showInHelpMessage() {
        return true;
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {

        int page = 1;

        if (args.length >= 2) {
            int totalPages = helpPages.getTotalPages();
            try {
                page = Integer.parseInt(args[1]);
                if (page < 1 || page > totalPages) {
                    commandManager.sendMessage(sender, "help-list-invalid-page", Map.of("{page}", args[1]));
                    return;
                }
            } catch (NumberFormatException ignored) {
                commandManager.sendMessage(sender, "help-list-invalid-page", Map.of("{page}", args[1]));
                return;
            }
        }

        if (isConsole) {
            helpPages.sendConsoleHelp(page);
        } else {
            helpPages.sendPlayerHelp((Player) sender, page);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 2) {
            int totalPages = helpPages.getTotalPages();

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