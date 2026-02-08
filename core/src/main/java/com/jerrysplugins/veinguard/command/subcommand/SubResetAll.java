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

import java.util.List;

public class SubResetAll implements ISubCommand {

    private final VeinGuard plugin;

    public SubResetAll(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "resetall";
    }

    @Override
    public String getDescription() {
        return "Reset all players block break history.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.resetall";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "resetall";
    }

    @Override
    public boolean showInHelpMessage() {
        return true;
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {

        if (args.length != 1) {
            commandManager.sendUsage(sender, getUsage(), isConsole);
            return;
        }

        plugin.getPlayerTracker().resetAllData();
        commandManager.sendMessage(sender, "reset-all", null);

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}