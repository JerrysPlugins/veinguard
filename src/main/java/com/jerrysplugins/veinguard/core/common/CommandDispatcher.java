package com.jerrysplugins.veinguard.core.common;

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

    /**
     * Dispatches a list of alert commands asynchronously.
     * Supports %player% placeholder replacement.
     *
     * @param commands     List of commands from config.
     * @param targetPlayer Optional player for placeholder replacement.
     */
    public void dispatchCommandsAsync(Set<String> commands, Player targetPlayer) {
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

    /**
     * Convenience method: pull alert commands from config and dispatch asynchronously.
     *
     * @param suspect The player involved in the alert
     */
    public void dispatchAlertCommandsAsync(Player suspect) {
        Set<String> alertCommands = plugin.getConfigOptions().getAlertCommands();
        dispatchCommandsAsync(alertCommands, suspect);
    }

    /**
     * Overload for async dispatch without a target player.
     *
     * @param commands List of commands
     */
    public void dispatchCommandsAsync(Set<String> commands) {
        dispatchCommandsAsync(commands, null);
    }

    /**
     * Replace placeholders in commands.
     *
     * @param command The command string
     * @param player  The player to replace %player%, can be null
     * @return Parsed command
     */
    private String parsePlaceholders(String command, Player player) {
        if (player != null) {
            command = command.replace("{player}", player.getName());
        }
        return command;
    }
}