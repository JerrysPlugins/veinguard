package com.jerrysplugins.veinguard.core.util.locale;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.util.logger.Level;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Locale {

    private final VeinGuard plugin;

    private static final Pattern STRIP_PATTERN = Pattern.compile("(?i)(§[0-9A-FK-OR])|(&[0-9A-FK-OR])|(&#[A-Fa-f0-9]{6})");
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");

    public Locale(VeinGuard plugin) {
        this.plugin = plugin;
    }

    public String getMessage(String key, boolean formatting) {
        String raw = plugin.getLangConfig().getString(key);
        if (raw == null) {
            plugin.getLog().log(Level.WARN, "Message key '" + key + "' is missing in lang.yml.");
            return ChatColor.RED + "Message key '" + ChatColor.AQUA + key + ChatColor.RED + "' is missing in lang.yml!";
        }
        return formatting ? translateColorCodes(raw) : stripColorCodes(raw);
    }

    public List<String> getListMessage(String key, boolean formatting) {
        List<String> rawList = plugin.getLangConfig().getStringList(key);

        if (rawList.isEmpty()) {
            plugin.getLog().log(Level.WARN, "Message list key '" + key + "' is missing or empty in lang.yml.");
            return Collections.emptyList();
        }

        List<String> formatted = new ArrayList<>(rawList.size());
        for (String line : rawList) {
            if (line == null) continue;
            formatted.add(formatting ? translateColorCodes(line) : stripColorCodes(line));
        }

        return formatted;
    }

    public static String stripColorCodes(String message) {
        if (message == null) return "";
        return STRIP_PATTERN.matcher(message).replaceAll("");
    }

    public String translateColorCodes(String message) {
        if (message == null) return "MESSAGE_NOT_FOUND";

        String colored = ChatColor.translateAlternateColorCodes('&', message);

        Matcher matcher = HEX_PATTERN.matcher(colored);
        StringBuilder out = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1);
            try {
                matcher.appendReplacement(out, Matcher.quoteReplacement(ChatColor.of(hex).toString()));
            } catch (IllegalArgumentException e) {
                plugin.getLog().log(Level.ERROR, "Invalid hex color code: " + hex);
                matcher.appendReplacement(out, "");
            }
        }
        matcher.appendTail(out);
        return out.toString();
    }
}