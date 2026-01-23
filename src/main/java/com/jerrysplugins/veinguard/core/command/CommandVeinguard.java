package com.jerrysplugins.veinguard.core.command;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.util.logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandVeinguard implements CommandExecutor, TabCompleter {

    private final VeinGuard plugin;
    private final List<String> validSubCommands = List.of(
            "check", "help", "msg", "mute", "reload", "reset", "resetall", "toggle-alerts", "unmute"
    );

    public CommandVeinguard(VeinGuard plugin) {
        this.plugin = plugin;
        var command = plugin.getCommand("veinguard");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        } else {
            plugin.getLog().log(Level.WARN, "Command 'veinguard' is not defined in plugin.yml!");
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            return handleConsoleCommand(args);
        }

        Player player = (Player) sender;
        return handlePlayerCommand(player, args);
    }

    /** ---------------------- CONSOLE LOGIC ---------------------- **/
    private boolean handleConsoleCommand(String[] args) {
        if (args.length == 0) {
            sendConsolePluginInfo();
            return false;
        }

        String subCommand = args[0].toLowerCase();
        if (!validSubCommands.contains(subCommand)) {
            plugin.getLog().log(Level.INFO, plugin.getLocale().getMessage("veinguard-usage", false));
            return false;
        }

        switch (subCommand) {
            case "check" -> handleCheckCommand(null, args, true);
            case "help" -> sendConsoleHelp();
            case "msg" -> handleMsgCommand(null, args, true);
            case "mute" -> handleMuteCommand(null, args, true);
            case "unmute" -> handleUnmuteCommand(null, args, true);
            case "reload" -> handleReloadCommand(null, args, true);
            case "reset" -> handleResetCommand(null, args, true);
            case "resetall" -> handleResetAllCommand(null, args, true);
            case "toggle-alerts" -> handleToggleAlertsCommand(null, args, true);
        }

        return false;
    }

    /** ---------------------- PLAYER LOGIC ---------------------- **/
    private boolean handlePlayerCommand(Player player, String[] args) {
        String prefix = plugin.getLocale().getMessage("plugin-prefix", true);

        if (!hasAccess(player)) {
            player.sendMessage(prefix + plugin.getLocale().getMessage("no-permission", true));
            return false;
        }

        if (args.length == 0) {
            sendPlayerPluginInfo(player);
            return false;
        }

        String subCommand = args[0].toLowerCase();
        if (!validSubCommands.contains(subCommand)) {
            player.sendMessage(prefix + plugin.getLocale().getMessage("veinguard-usage", true));
            return false;
        }

        if (!hasSubCommandAccess(player, subCommand)) {
            player.sendMessage(prefix + plugin.getLocale().getMessage("no-permission", true));
            return false;
        }

        switch (subCommand) {
            case "check" -> handleCheckCommand(player, args, false);
            case "help" -> sendPlayerHelp(player);
            case "msg" -> handleMsgCommand(player, args, false);
            case "mute" -> handleMuteCommand(player, args, false);
            case "unmute" -> handleUnmuteCommand(player, args, false);
            case "reload" -> handleReloadCommand(player, args, false);
            case "reset" -> handleResetCommand(player, args, false);
            case "resetall" -> handleResetAllCommand(player, args, false);
            case "toggle-alerts" -> handleToggleAlertsCommand(player, args, false);
        }

        return false;
    }

    /** ---------------------- COMMON HELP / INFO ---------------------- **/
    private void sendConsolePluginInfo() {
        List<String> helpMessage = plugin.getLocale().getListMessage("plugin-info", true);
        for (String line : helpMessage) {
            plugin.getLog().log(Level.INFO, line
                    .replace("{description}", plugin.getPluginDescription())
                    .replace("{version}", plugin.getPluginVersion())
                    .replace("{author}", plugin.getPluginAuthors())
                    .replace("{website}", plugin.getPluginWebsite()));
        }
    }

    private void sendPlayerPluginInfo(Player player) {
        List<String> infoMessage = plugin.getLocale().getListMessage("plugin-info", true);
        for (String line : infoMessage) {
            player.sendMessage(line
                    .replace("{description}", plugin.getPluginDescription())
                    .replace("{version}", plugin.getPluginVersion())
                    .replace("{author}", plugin.getPluginAuthors())
                    .replace("{website}", plugin.getPluginWebsite()));
        }
    }

    private void sendConsoleHelp() {
        List<String> helpMessage = plugin.getLocale().getListMessage("help", false);
        for (String line : helpMessage) {
            plugin.getLog().log(Level.INFO, line);
        }
    }

    private void sendPlayerHelp(Player player) {
        List<String> helpMessage = plugin.getLocale().getListMessage("help", true);
        for (String line : helpMessage) {
            player.sendMessage(line);
        }
    }

    /** ---------------------- PERMISSION CHECKS ---------------------- **/
    private boolean hasAccess(Player player) {
        if (player.isOp()) return true;
        for (String sub : validSubCommands) {
            if (player.hasPermission("veinguard.command." + sub)) return true;
        }
        return false;
    }

    private boolean hasSubCommandAccess(Player player, String subCommand) {
        return player.isOp() || player.hasPermission("veinguard.command." + subCommand);
    }

    /** ---------------------- COMMAND HANDLERS ---------------------- **/
    private void handleCheckCommand(Player player, String[] args, boolean isConsole) {
        if (args.length == 1) {
            sendUsage(player, "veinguard-usage-check", isConsole);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(player, "player-not-found", Map.of("{player}", args[1]), isConsole);
            return;
        }

        if (plugin.getPlayerTracker().isPlayerTracked(target)) {
            sendMessage(player, "report-not-tracked", Map.of("{player}", target.getName()), isConsole);
            return;
        }

        int page = 1;
        if (args.length == 3) {
            int totalPages = plugin.getBlockReport().getPlayerReportPages(target);
            try {
                page = Integer.parseInt(args[2]);
                if (page > totalPages) {
                    sendMessage(player, "report-invalid-page", Map.of("{page}", args[2]), isConsole);
                    return;
                }
            } catch (NumberFormatException e) {
                sendMessage(player, "report-invalid-page", Map.of("{page}", args[2]), isConsole);
                return;
            }
        }

        if (isConsole) {
            plugin.getBlockReport().sendConsoleReport(target, page);
        } else {
            plugin.getBlockReport().sendPlayerReport(player, target, page);
        }
    }

    private void handleMsgCommand(Player player, String[] args, boolean isConsole) {
        if (args.length == 1) {
            sendUsage(player, "veinguard-usage-msg", isConsole);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(player, "player-not-found", Map.of("{player}", args[1]), isConsole);
            return;
        }

        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
        target.sendMessage(plugin.getLocale().translateColorCodes(message));
    }

    private void handleMuteCommand(Player player, String[] args, boolean isConsole) {
        if (args.length != 2) {
            sendUsage(player, "veinguard-usage-mute", isConsole);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(player, "player-not-found", Map.of("{player}", args[1]), isConsole);
            return;
        }

        if (plugin.getPlayerTracker().isPlayerMuted(target)) {
            sendMessage(player, "already-muted", Map.of("{player}", target.getName()), isConsole);
            return;
        }

        plugin.getPlayerTracker().mutePlayer(target);
        sendMessage(player, "mute-player", Map.of("{player}", target.getName()), isConsole);
    }

    private void handleUnmuteCommand(Player player, String[] args, boolean isConsole) {
        if (args.length != 2) {
            sendUsage(player, "veinguard-usage-unmute", isConsole);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(player, "player-not-found", Map.of("{player}", args[1]), isConsole);
            return;
        }

        if (!plugin.getPlayerTracker().isPlayerMuted(target)) {
            sendMessage(player, "already-unmuted", Map.of("{player}", target.getName()), isConsole);
            return;
        }

        plugin.getPlayerTracker().unmutePlayer(target);
        sendMessage(player, "unmute-player", Map.of("{player}", target.getName()), isConsole);
    }

    private void handleReloadCommand(Player player, String[] args, boolean isConsole) {
        if (args.length != 1) {
            sendUsage(player, "veinguard-usage-reload", isConsole);
            return;
        }

        boolean success = plugin.reload();
        sendMessage(player, success ? "reload-success" : "reload-failed", null, isConsole);
    }

    private void handleResetCommand(Player player, String[] args, boolean isConsole) {
        if (args.length != 2) {
            sendUsage(player, "veinguard-usage-reset", isConsole);
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sendMessage(player, "player-not-found", Map.of("{player}", args[1]), isConsole);
            return;
        }

        plugin.getPlayerTracker().resetPlayerData(target);
        sendMessage(player, "reset-player", Map.of("{player}", target.getName()), isConsole);
    }

    private void handleResetAllCommand(Player player, String[] args, boolean isConsole) {
        if (args.length != 1) {
            sendUsage(player, "veinguard-usage-resetall", isConsole);
            return;
        }

        plugin.getPlayerTracker().resetAllData();
        sendMessage(player, "reset-all", null, isConsole);
    }

    private void handleToggleAlertsCommand(Player player, String[] args, boolean isConsole) {
        if (isConsole || args.length == 2) {
            if (args.length == 2 && player != null && !player.hasPermission("veinguard.toggle-alerts.others")) {
                player.sendMessage(plugin.getLocale().getMessage("no-permission", true));
                return;
            }
            Player target = Bukkit.getPlayer(args.length == 2 ? args[1] : player.getName());
            if (target == null) {
                sendMessage(player, "player-not-found", Map.of("{player}", args[1]), isConsole);
                return;
            }

            if (!plugin.getAlertManager().canReceiveAlerts(target)) {
                sendMessage(player, args.length == 2 ? "toggle-alerts-others-not-staff" : "toggle-alerts-self-not-staff",
                        Map.of("{player}", target.getName()), isConsole);
                return;
            }

            if (plugin.getPlayerTracker().isStaffMuted(target)) {
                plugin.getPlayerTracker().unmuteStaff(target);
                sendMessage(player, args.length == 2 ? "toggle-alerts-others-on" : "toggle-alerts-self-on",
                        Map.of("{player}", target.getName()), isConsole);
            } else {
                plugin.getPlayerTracker().muteStaff(target);
                sendMessage(player, args.length == 2 ? "toggle-alerts-others-off" : "toggle-alerts-self-off",
                        Map.of("{player}", target.getName()), isConsole);
            }
            return;
        }

        if (player != null && args.length == 1) {
            if (!plugin.getAlertManager().canReceiveAlerts(player)) {
                sendMessage(player, "toggle-alerts-self-not-staff", null, false);
                return;
            }

            if (plugin.getPlayerTracker().isStaffMuted(player)) {
                plugin.getPlayerTracker().unmuteStaff(player);
                sendMessage(player, "toggle-alerts-self-on", null, false);
            } else {
                plugin.getPlayerTracker().muteStaff(player);
                sendMessage(player, "toggle-alerts-self-off", null, false);
            }
        }
    }

    /** ---------------------- UTILITY METHODS ---------------------- **/
    private void sendUsage(Player player, String key, boolean isConsole) {
        if (isConsole) {
            plugin.getLog().log(Level.INFO, plugin.getLocale().getMessage(key, false));
        } else if (player != null) {
            String prefix = plugin.getLocale().getMessage("plugin-prefix", true);
            player.sendMessage(prefix + plugin.getLocale().getMessage(key, true));
        }
    }

    private void sendMessage(Player player, String key, Map<String, String> placeholders, boolean isConsole) {
        String message = plugin.getLocale().getMessage(key, !isConsole);

        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace(entry.getKey(), entry.getValue());
            }
        }

        if (isConsole) {
            plugin.getLog().log(Level.INFO, message);
        } else if (player != null) {
            String prefix = plugin.getLocale().getMessage("plugin-prefix", true);
            player.sendMessage(prefix + message);
        }
    }

    @Override
    public List<String> onTabComplete(@NonNull CommandSender sender, @NonNull Command cmd,
                                      @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player player)) return null;

        if (args.length == 1) {
            return validSubCommands.stream()
                    .filter(sub -> player.hasPermission("veinguard.command." + sub) || player.isOp())
                    .filter(sub -> sub.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (player.hasPermission("veinguard.command." + subCommand) || player.isOp()) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("check")) {
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) return null;

            List<String> pages = new ArrayList<>();
            pages.add("<Page #>");
            int totalPages = plugin.getBlockReport().getPlayerReportPages(target);
            for (int i = 1; i <= totalPages; i++) pages.add(String.valueOf(i));
            return pages;
        }

        return null;
    }
}