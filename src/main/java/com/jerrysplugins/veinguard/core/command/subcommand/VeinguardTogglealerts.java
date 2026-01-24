package com.jerrysplugins.veinguard.core.command.subcommand;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.command.CommandManager;
import com.jerrysplugins.veinguard.core.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class VeinguardTogglealerts implements SubCommand {

    private final VeinGuard plugin;

    public VeinguardTogglealerts(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "toggle-alerts";
    }

    @Override
    public String getDescription() {
        return "Toggle alerts for yourself or another player.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.toggle-alerts";
    }

    @Override
    public String getUsage() {
        return "toggle-alerts [player]";
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {

        Player player = sender instanceof Player ? (Player) sender : null;

        if (args.length > 2 || (isConsole && args.length == 1)) {
            commandManager.sendUsage(sender, getUsage(), isConsole);
            return;
        }

        Player target;

        if (args.length == 2) {
            if (player != null && !player.hasPermission("veinguard.toggle-alerts.others")) {
                commandManager.sendMessage(sender, "no-permission", null);
                return;
            }

            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                commandManager.sendMessage(sender, "player-not-found", Map.of("{player}", args[1]));
                return;
            }
        } else {
            target = player;
        }

        if (!plugin.getAlertManager().canReceiveAlerts(target)) {
            commandManager.sendMessage(
                    sender,
                    args.length == 2 ? "toggle-alerts-others-not-staff" : "toggle-alerts-self-not-staff",
                    Map.of("{player}", target.getName())
            );
            return;
        }

        if (plugin.getPlayerTracker().isStaffMuted(target)) {
            plugin.getPlayerTracker().unmuteStaff(target);
            commandManager.sendMessage(
                    sender,
                    args.length == 2 ? "toggle-alerts-others-on" : "toggle-alerts-self-on",
                    Map.of("{player}", target.getName())
            );
        } else {
            plugin.getPlayerTracker().muteStaff(target);
            commandManager.sendMessage(
                    sender,
                    args.length == 2 ? "toggle-alerts-others-off" : "toggle-alerts-self-off",
                    Map.of("{player}", target.getName())
            );
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 2 && sender instanceof Player player) {
            if (player.hasPermission("veinguard.toggle-alerts.others")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .toList();
            }
        }

        return List.of();
    }
}