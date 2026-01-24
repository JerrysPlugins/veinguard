package com.jerrysplugins.veinguard.core.command.subcommand;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.command.CommandManager;
import com.jerrysplugins.veinguard.core.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class VeinguardMute implements SubCommand {

    private final VeinGuard plugin;

    public VeinguardMute(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "mute";
    }

    @Override
    public String getDescription() {
        return "Mute and suppress a players alerts.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.mute";
    }

    @Override
    public String getUsage() {
        return "mute <player>";
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

        if (plugin.getPlayerTracker().isPlayerMuted(target)) {
            commandManager.sendMessage(sender, "already-muted", Map.of("{player}", target.getName()));
            return;
        }

        plugin.getPlayerTracker().mutePlayer(target);
        commandManager.sendMessage(sender, "mute-player", Map.of("{player}", target.getName()));
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