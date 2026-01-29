package com.jerrysplugins.veinguard.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    /**
     * Primary name of the subcommand.
     * Example: "reload"
     */
    String getName();

    /**
     * Description of the sub-commands functions.
     */
    String getDescription();

    /**
     * Full permission required to execute this subcommand.
     * Example: "veinguard.command.reload"
     */
    String getPermission();

    /**
     * Usage fragment WITHOUT /veinguard.
     * Example: "reload" or "check <player> [page]"
     */
    String getUsage();

    /**
     * Execute the subcommand.
     */
    void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole);

    /**
     * Tab completion beyond the subcommand name.
     */
    List<String> tabComplete(CommandSender sender, String[] args);
}