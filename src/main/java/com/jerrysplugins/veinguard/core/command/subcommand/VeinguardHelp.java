package com.jerrysplugins.veinguard.core.command.subcommand;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.command.CommandManager;
import com.jerrysplugins.veinguard.core.command.SubCommand;
import com.jerrysplugins.veinguard.core.util.logger.Level;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VeinguardHelp implements SubCommand {

    private final VeinGuard plugin;

    public VeinguardHelp(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "View the plugin help message.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.help";
    }

    @Override
    public String getUsage() {
        return "help";
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {

        if(args.length != 1) {
            commandManager.sendUsage(sender, getUsage(), isConsole);
            return;
        }

        List<String> helpMessage;

        if (isConsole) {
            helpMessage = plugin.getLocale().getListMessage("help", false);
            for (String line : helpMessage) {
                plugin.getLog().log(Level.INFO, line);
            }
        } else {
            helpMessage = plugin.getLocale().getListMessage("help", true);
            for (String line : helpMessage) {
                sender.sendMessage(line);
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return List.of();
    }
}