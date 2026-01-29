package com.jerrysplugins.veinguard.core;

import com.jerrysplugins.veinguard.VeinGuard;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class CommandDispatcher {

    private final VeinGuard plugin;
    private final ConsoleCommandSender console;

    public CommandDispatcher(VeinGuard plugin) {
        this.plugin = plugin;
        this.console = Bukkit.getServer().getConsoleSender();
    }

    private void dispatchCommandsAsync(Set<String> commands, Player targetPlayer) {
        if (commands == null || commands.isEmpty()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (String command : commands) {
                if (command == null || command.isBlank()) continue;

                String parsedCommand = parsePlaceholders(command, targetPlayer);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.dispatchCommand(console, parsedCommand);
                });
            }
        });
    }

    public void dispatchAlertCommandsAsync(Player suspect) {
        Set<String> alertCommands = plugin.getConfigOptions().getAlertCommands();
        dispatchCommandsAsync(alertCommands, suspect);
    }

    public void dispatchCommandsAsync(Set<String> commands) {
        dispatchCommandsAsync(commands, null);
    }

    private String parsePlaceholders(String command, Player player) {
        if (player != null) {
            command = command.replace("{player}", player.getName());
        }
        return command;
    }
}