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
import com.jerrysplugins.veinguard.common.ConfigOptions;
import com.jerrysplugins.veinguard.common.pagination.TrackedBlockPages;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SubTrackedBlocks implements ISubCommand {

    private final VeinGuard plugin;

    private final TrackedBlockPages trackedBlockPages;

    public SubTrackedBlocks(VeinGuard plugin) {
        this.plugin = plugin;
        this.trackedBlockPages = new TrackedBlockPages(plugin);
    }

    @Override
    public String getName() {
        return "tracked-blocks";
    }

    @Override
    public String getDescription() {
        return "Add, remove or list currently tracked blocks.";
    }

    @Override
    public String getPermission() {
        return "veinguard.command.tracked-blocks";
    }

    @Override
    public List<String> getSubPermissions() {
        return List.of(
                "veinguard.command.tracked-blocks.add",
                "veinguard.command.tracked-blocks.list",
                "veinguard.command.tracked-blocks.remove"
        );
    }

    @Override
    public String getUsage() {
        return "tracked-blocks <add|list|remove> (?list [page]) <block> <threshold> <pretty-name>";
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

        String subCommand = args[1].toLowerCase();
        ConfigOptions configOptions = plugin.getConfigOptions();

        switch (subCommand) {
            case "add" -> {

                if(!isConsole && !sender.hasPermission("veinguard.command.tracked-blocks.add")) {
                    commandManager.sendMessage(sender, "no-permission", null);
                    return;
                }

                if(args.length < 5) {
                    commandManager.sendUsage(sender, getUsage(), isConsole);
                    return;
                }

                Material material;
                int threshold;

                try {
                    material = Material.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException ignored) {
                    commandManager.sendMessage(sender, "tracked-blocks-invalid-material", Map.of("{material}", args[2]));
                    return;
                }

                boolean isBlock = material.isBlock();

                if(!isBlock) {
                    commandManager.sendMessage(sender, "tracked-blocks-invalid-material", Map.of("{material}", args[2]));
                    return;
                }

                try {
                    threshold = Integer.parseInt(args[3]);
                } catch (NumberFormatException ignored) {
                    commandManager.sendMessage(sender, "tracked-blocks-invalid-threshold", Map.of("{threshold}", args[3]));
                    return;
                }

                String prettyName = String.join(" ", Arrays.copyOfRange(args, 4, args.length));

                configOptions.addOrUpdateTrackedBlock(material, threshold,  prettyName);

                commandManager.sendMessage(sender, "tracked-blocks-added",
                        Map.of("{material}", material.name(),
                                "{threshold}", String.valueOf(threshold),
                                "{prettyName}", prettyName)
                );
            }

            case "list" -> {

                if(!isConsole && !sender.hasPermission("veinguard.command.tracked-blocks.list")) {
                    commandManager.sendMessage(sender, "no-permission", null);
                    return;
                }

                int page = 1;

                if (args.length == 3) {
                    int totalPages = trackedBlockPages.getTotalPages();
                    try {
                        page = Integer.parseInt(args[2]);
                        if (page < 1 || page > totalPages) {
                            commandManager.sendMessage(sender, "tracked-blocks-list-invalid-page", Map.of("{page}", args[2]));
                            return;
                        }
                    } catch (NumberFormatException ignored) {
                        commandManager.sendMessage(sender, "tracked-blocks-list-invalid-page", Map.of("{page}", args[2]));
                        return;
                    }
                }

                if (isConsole) {
                    trackedBlockPages.sendConsoleList(page);
                } else {
                    trackedBlockPages.sendPlayerList((Player) sender, page);
                }
            }

            case "remove" -> {

                if(!isConsole && !sender.hasPermission("veinguard.command.tracked-blocks.remove")) {
                    commandManager.sendMessage(sender, "no-permission", null);
                    return;
                }

                if(args.length < 3) {
                    commandManager.sendUsage(sender, getUsage(), isConsole);
                    return;
                }

                Material material;

                try {
                    material = Material.valueOf(args[2].toUpperCase());
                } catch (Exception e) {
                    commandManager.sendMessage(sender, "tracked-blocks-invalid-material", Map.of("{material}", args[2]));
                    return;
                }

                boolean isTracked = configOptions.isTrackedMaterial(material);
                if(!isTracked) {
                    commandManager.sendMessage(sender, "tracked-blocks-not-tracked",
                            Map.of("{material}", material.name())
                    );
                    return;
                }

                configOptions.removeTrackedBlock(material);

                commandManager.sendMessage(sender, "tracked-blocks-removed",
                        Map.of("{material}", material.name())
                );
            }

            default -> commandManager.sendUsage(sender, getUsage(), isConsole);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {

        if (args.length == 2) {
            return Stream.of("add", "list", "remove")
                    .filter(sub -> {
                        String perm = "veinguard.command.tracked-blocks." + sub;
                        return sender.isOp() || sender.hasPermission(perm);
                    })
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .toList();
        }

        String sub = args[1].toLowerCase();

        switch (sub) {
            case "add" -> {
                if (args.length == 3) {
                    String input = args[2].toUpperCase();

                    return Arrays.stream(Material.values())
                            .filter(Material::isBlock)
                            .map(Material::name)
                            .filter(name -> name.startsWith(input))
                            .sorted()
                            .toList();
                }
            }

            case "remove" -> {
                if (args.length == 3) {
                    String input = args[2].toUpperCase();

                    return plugin.getConfigOptions().getTrackedBlocks().keySet().stream()
                            .map(Material::name)
                            .filter(name -> name.startsWith(input))
                            .sorted()
                            .toList();
                }
            }

            case "list" -> {
                if (args.length == 3) {
                    int totalPages = trackedBlockPages.getTotalPages();

                    List<String> pages = new java.util.ArrayList<>();
                    pages.add("<Page #>");
                    for (int i = 1; i <= totalPages; i++) {
                        pages.add(String.valueOf(i));
                    }
                    return pages;
                }
            }
        }
        return List.of();
    }
}