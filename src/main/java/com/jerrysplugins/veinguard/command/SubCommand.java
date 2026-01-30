package com.jerrysplugins.veinguard.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    String getName();

    String getDescription();

    String getPermission();

    String getUsage();

    void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole);

    List<String> tabComplete(CommandSender sender, String[] args);
}