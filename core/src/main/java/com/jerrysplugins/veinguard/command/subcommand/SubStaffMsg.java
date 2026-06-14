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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubStaffMsg implements ISubCommand {

    private final VeinGuard plugin;

    public SubStaffMsg(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "staffmsg";
    }

    @Override
    public String getDescription() {
        return "Send a formatted message to all online staff with the notify permission.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.staffmsg";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "staffmsg <message>";
    }

    @Override
    public boolean showInHelpMessage() {
        return true;
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {

        if (args.length == 1) {
            commandManager.sendUsage(sender, getUsage(), isConsole);
            return;
        }

        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        String translated = plugin.getLocale().translateColorCodes(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("veinguard.notify")) {
                player.sendMessage(translated);
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}
