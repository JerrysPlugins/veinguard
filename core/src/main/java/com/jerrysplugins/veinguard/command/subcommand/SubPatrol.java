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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SubPatrol implements ISubCommand {

    private final VeinGuard plugin;

    public SubPatrol(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "patrol";
    }

    @Override
    public String getDescription() {
        return "Patrol through all online players.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.patrol";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "patrol <start/stop/pause/resume/next/back>";
    }

    @Override
    public boolean showInHelpMessage() {
        return true;
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {
        if (isConsole) {
            commandManager.sendMessage(sender, "in-game-only", null);
            return;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            commandManager.sendUsage(sender, getUsage(), false);
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "start":
                plugin.getPatrolManager().startPatrol(player);
                break;
            case "stop":
                plugin.getPatrolManager().stopPatrol(player);
                break;
            case "pause":
                plugin.getPatrolManager().pausePatrol(player);
                break;
            case "resume":
                plugin.getPatrolManager().resumePatrol(player);
                break;
            case "next":
                plugin.getPatrolManager().nextPatrol(player);
                break;
            case "back":
                plugin.getPatrolManager().backPatrol(player);
                break;
            default:
                commandManager.sendUsage(sender, getUsage(), false);
                break;
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("start", "stop", "pause", "resume", "next", "back");
        }
        return List.of();
    }
}
