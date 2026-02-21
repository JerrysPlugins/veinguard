/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface ISubCommand {

    String getName();

    String getDescription();

    String getPermission();

    List<String> getSubPermissions();

    String getUsage();

    boolean showInHelpMessage();

    void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole);

    List<String> tabComplete(CommandSender sender, String[] args);
}