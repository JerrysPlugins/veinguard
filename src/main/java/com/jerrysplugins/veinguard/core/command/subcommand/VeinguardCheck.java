package com.jerrysplugins.veinguard.core.command.subcommand;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.command.CommandManager;
import com.jerrysplugins.veinguard.core.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class VeinguardCheck implements SubCommand {

    private final VeinGuard plugin;

    public VeinguardCheck(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public String getDescription() {
        return "Check a player's block break report.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.check";
    }

    @Override
    public String getUsage() {
        return "check <player> [page]";
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {

        if (args.length < 2) {
            commandManager.sendUsage(sender, getUsage(), isConsole);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            commandManager.sendMessage(sender, "player-not-found", Map.of("{player}", args[1]));
            return;
        }

        if (plugin.getPlayerTracker().isPlayerTracked(target)) {
            commandManager.sendMessage(sender, "report-not-tracked", Map.of("{player}", target.getName()));
            return;
        }

        int page = 1;

        if (args.length == 3) {
            int totalPages = plugin.getBlockReport().getPlayerReportPages(target);
            try {
                page = Integer.parseInt(args[2]);
                if (page < 1 || page > totalPages) {
                    commandManager.sendMessage(sender, "report-invalid-page", Map.of("{page}", args[2]));
                    return;
                }
            } catch (NumberFormatException ignored) {
                commandManager.sendMessage(sender, "report-invalid-page", Map.of("{page}", args[2]));
                return;
            }
        }

        if (isConsole) {
            plugin.getBlockReport().sendConsoleReport(target, page);
        } else {
            plugin.getBlockReport().sendPlayerReport((Player) sender, target, page);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        if (args.length == 3) {
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) return List.of();

            int totalPages = plugin.getBlockReport().getPlayerReportPages(target);

            List<String> pages = new java.util.ArrayList<>();
            pages.add("<Page #>");
            for (int i = 1; i <= totalPages; i++) {
                pages.add(String.valueOf(i));
            }
            return pages;
        }

        return List.of();
    }
}