/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.command;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.command.subcommand.*;
import com.jerrysplugins.veinguard.util.VGUtils;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {

    private final VeinGuard plugin;
    private final Map<String, ISubCommand> subCommands = new HashMap<>();

    public CommandManager(VeinGuard plugin) {
        this.plugin = plugin;

        plugin.getLog().log(Level.DEBUG, "Registering command 'veinguard' in CommandManager.class");

        PluginCommand command = plugin.getCommand("veinguard");
        if (command == null) {
            plugin.getLog().log(Level.WARN, "Command 'veinguard' missing from plugin.yml");
            return;
        }

        command.setExecutor(this);
        command.setTabCompleter(this);

        register(new VeinguardCheck(plugin));
        register(new VeinguardHelp(plugin));
        register(new VeinguardMsg(plugin));
        register(new VeinguardMute(plugin));
        register(new VeinguardReload(plugin));
        register(new VeinguardResetall(plugin));
        register(new VeinguardReset(plugin));
        register(new VeinguardToggleAlerts(plugin));
        register(new VeinguardTrackedBlocks(plugin));
        register(new VeinguardUnmute(plugin));
    }

    public void register(ISubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        boolean isConsole = !(sender instanceof Player);

        String pluginPrefix = plugin.getLocale().getMessage("plugin-prefix", true);

        if (!isConsole && !hasAccess(sender)) {
            sender.sendMessage(pluginPrefix + plugin.getLocale().getMessage("no-permission", true));
            return true;
        }

        if (args.length == 0) {
            sendPluginInfo(sender, isConsole);
            return true;
        }

        ISubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) {
            sendUsage(sender, null, isConsole);
            return true;
        }

        if (!isConsole && !sender.isOp() &&
                !sender.hasPermission(sub.getPermission()) &&
                sub.getSubPermissions().stream().noneMatch(sender::hasPermission)) {

            sender.sendMessage(pluginPrefix + plugin.getLocale().getMessage("no-permission", true));
            return true;
        }

        sub.execute(this, sender, args, isConsole);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

        boolean isConsole = !(sender instanceof Player);

        if (!isConsole && !hasAccess(sender)) {
            return List.of();
        }

        if (args.length == 1) {
            return subCommands.values().stream()
                    .filter(sub -> {
                        if (sender.isOp()) return true;
                        if (sender.hasPermission(sub.getPermission())) return true;

                        List<String> subPerms = sub.getSubPermissions();
                        if (subPerms == null) return false;

                        return subPerms.stream()
                                .anyMatch(sender::hasPermission);
                    })
                    .map(ISubCommand::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        ISubCommand sub = subCommands.get(args[0].toLowerCase());
        if (sub == null) return List.of();

        boolean hasMainOrSubPerm = sender.isOp()
                || sender.hasPermission(sub.getPermission())
                || (sub.getSubPermissions() != null && sub.getSubPermissions().stream().anyMatch(sender::hasPermission));

        if (!hasMainOrSubPerm) {
            return List.of();
        }

        return sub.tabComplete(sender, args);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasAccess(CommandSender sender) {
        if (sender.isOp()) return true;

        return subCommands.values().stream()
                .anyMatch(sub -> {
                    if (sender.hasPermission(sub.getPermission())) return true;

                    List<String> subPerms = sub.getSubPermissions();
                    return subPerms != null && subPerms.stream().anyMatch(sender::hasPermission);
                });
    }

    public void sendUsage(CommandSender sender, String usage, boolean isConsole) {
        if (usage == null || usage.isBlank()) {
            usage = "<subcommand>";
        }

        String pluginPrefix = plugin.getLocale().getMessage("plugin-prefix", true);
        String base = plugin.getLocale().getMessage("command-usage", !isConsole);
        String message = base.replace("{usage}", usage);

        if (isConsole) {
            plugin.getLog().log(Level.INFO, message);
        } else {
            sender.sendMessage(pluginPrefix + message);
        }
    }

    public void sendMessage(CommandSender sender, String key, Map<String, String> placeholders) {

        boolean isConsole = !(sender instanceof Player);

        String message = plugin.getLocale().getMessage(key, !isConsole);

        if (placeholders != null) {
            message = VGUtils.applyPlaceholders(message, placeholders);
        }

        if (isConsole) {
            plugin.getLog().log(Level.INFO, message);
            return;
        }

        Player player = (Player) sender;
        String pluginPrefix = plugin.getLocale().getMessage("plugin-prefix", true);
        player.sendMessage(pluginPrefix + message);
    }

    private void sendPluginInfo(CommandSender sender, boolean isConsole) {
        List<String> lines = plugin.getLocale().getListMessage("plugin-info", !isConsole);

        for (String line : lines) {
            String replaced = line
                    .replace("{description}", plugin.getPluginDescription())
                    .replace("{version}", plugin.getPluginVersion())
                    .replace("{author}", plugin.getPluginAuthors())
                    .replace("{website}", plugin.getPluginWebsite());

            if (isConsole) {
                plugin.getLog().log(Level.NONE, replaced);
            } else {
                sender.sendMessage(replaced);
            }
        }
    }
}