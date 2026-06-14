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
import com.jerrysplugins.veinguard.common.pagination.TopPlayerPages;
import com.jerrysplugins.veinguard.util.VGUtils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;

public class SubTop implements ISubCommand {

    private final VeinGuard plugin;
    private final TopPlayerPages topPlayerPages;

    public SubTop(VeinGuard plugin) {
        this.plugin = plugin;
        this.topPlayerPages = new TopPlayerPages(plugin);
    }

    @Override
    public String getName() {
        return "top";
    }

    @Override
    public String getDescription() {
        return "Show top violators by blocks found.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.top";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "top [time] [page]";
    }

    @Override
    public boolean showInHelpMessage() {
        return true;
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {
        long sinceMillis = 0;
        int page = 1;
        String timeDisplay = plugin.getLocale().getMessage("top-all-time", !isConsole);

        if (args.length > 1) {
            long duration = VGUtils.parseTimeStringToMillis(args[1]);
            if (duration > 0) {
                sinceMillis = System.currentTimeMillis() - duration;
                timeDisplay = args[1];

                if (args.length > 2) {
                    try {
                        page = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        commandManager.sendMessage(sender, "top-invalid-page", Map.of("{page}", args[2]));
                        return;
                    }
                }
            } else {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    commandManager.sendMessage(sender, "top-invalid-time", Map.of("{time}", args[1]));
                    return;
                }
            }
        }

        int totalPages = topPlayerPages.getTotalPages(sinceMillis);
        if (page < 1 || page > totalPages) {
            commandManager.sendMessage(sender, "top-invalid-page", Map.of("{page}", String.valueOf(page)));
            return;
        }

        topPlayerPages.sendTopReport(sender, sinceMillis, timeDisplay, page);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("1h", "2h", "1d", "7d", "30d");
        }
        if (args.length == 3) {
            return List.of("1", "2", "3");
        }
        return List.of();
    }
}
