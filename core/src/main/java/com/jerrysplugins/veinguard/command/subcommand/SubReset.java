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
import java.util.Map;

public class SubReset implements ISubCommand {

    private final VeinGuard plugin;

    public SubReset(VeinGuard plugin) {
        this.plugin = plugin;
    }


    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Reset a player's block break history.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.reset";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "reset <player>";
    }

    @Override
    public boolean showInHelpMessage() {
        return true;
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {

        if (args.length != 2) {
            commandManager.sendUsage(sender, getUsage(), isConsole);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            commandManager.sendMessage(sender, "player-not-found", Map.of("{player}", args[1]));
            return;
        }

        plugin.getPlayerTracker().resetPlayerData(target);
        commandManager.sendMessage(sender, "reset-player", Map.of("{player}", target.getName()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}