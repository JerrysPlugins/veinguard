package com.jerrysplugins.veinguard.command.subcommand;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.command.CommandManager;
import com.jerrysplugins.veinguard.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VeinguardReload implements SubCommand {

    private final VeinGuard plugin;

    public VeinguardReload(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin and configuration files.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.reload";
    }

    @Override
    public String getUsage() {
        return "reload";
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