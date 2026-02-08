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

public class SubReload implements ISubCommand {

    private final VeinGuard plugin;

    public SubReload(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin configuration files.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.reload";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "reload";
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

        boolean success = plugin.reload();

        commandManager.sendMessage(sender, success ? "reload-success" : "reload-failed", null);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}