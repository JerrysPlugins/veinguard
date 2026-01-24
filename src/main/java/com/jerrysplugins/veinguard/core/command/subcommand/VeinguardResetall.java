package com.jerrysplugins.veinguard.core.command.subcommand;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.command.CommandManager;
import com.jerrysplugins.veinguard.core.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VeinguardResetall implements SubCommand {

    private final VeinGuard plugin;

    public VeinguardResetall(VeinGuard plugin) {
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
    public String getUsage() {
        return "resetall";
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