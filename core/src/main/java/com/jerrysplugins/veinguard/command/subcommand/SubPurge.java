/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard.command.subcommand;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.command.CommandManager;
import com.jerrysplugins.veinguard.command.ISubCommand;
import com.jerrysplugins.veinguard.util.VGUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SubPurge implements ISubCommand {

    private final VeinGuard plugin;

    public SubPurge(VeinGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "purge";
    }

    @Override
    public String getDescription() {
        return "Purge alert data from the database.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.purge";
    }

    @Override
    public List<String> getSubPermissions() {
        return null;
    }

    @Override
    public String getUsage() {
        return "purge <time> [player]";
    }

    @Override
    public boolean showInHelpMessage() {
        return true;
    }

    @Override
    public void execute(CommandManager commandManager, CommandSender sender, String[] args, boolean isConsole) {
        if (args.length < 2) {
            commandManager.sendUsage(sender, getUsage(), isConsole);
            return;
        }

        String timeStr = args[1];
        long durationMillis = VGUtils.parseTimeStringToMillis(timeStr);

        if (durationMillis <= 0) {
            commandManager.sendMessage(sender, "purge-invalid-time", Map.of("{time}", timeStr));
            return;
        }

        if (!isConsole && durationMillis < 1296000000L) {
            commandManager.sendMessage(sender, "purge-min-time", null);
            return;
        }

        long olderThanMillis = System.currentTimeMillis() - durationMillis;
        String tablePrefix = plugin.getConfigOptions().getDbTablePrefix();

        if (args.length > 2) {

            String targetName = args[2];

            CompletableFuture.supplyAsync(() -> {

                @SuppressWarnings("deprecation")
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                UUID targetUUID = target.getUniqueId();
                return plugin.getVGDatabase().purgeAlerts(targetUUID, olderThanMillis, tablePrefix);
            }).thenAccept(count -> Bukkit.getScheduler().runTask(plugin, () ->
                commandManager.sendMessage(sender, "purge-success", Map.of("{count}", String.valueOf(count)))));
        } else {

            CompletableFuture.supplyAsync(() -> plugin.getVGDatabase().purgeAlerts(olderThanMillis, tablePrefix))
                    .thenAccept(count -> Bukkit.getScheduler().runTask(plugin, () ->
                        commandManager.sendMessage(sender, "purge-success", Map.of("{count}", String.valueOf(count)))));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return List.of("1h", "1d", "15d", "30d");
        }
        if (args.length == 3) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
