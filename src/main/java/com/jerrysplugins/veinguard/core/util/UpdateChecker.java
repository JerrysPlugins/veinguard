package com.jerrysplugins.veinguard.core.util;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.util.logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker implements Listener {

    private final VeinGuard plugin;

    private volatile boolean updateAvailable = false;
    private final boolean announceUpdate;

    private final String latestVersionUrl = "https://raw.githubusercontent.com/JerrysPlugins/veinguard/refs/heads/master/version.txt";
    private volatile String latestVersion;

    public UpdateChecker(VeinGuard plugin) {
        this.plugin = plugin;
        this.announceUpdate = plugin.getConfig().getBoolean("show-update-notice", true);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        retrieveLatestVersion(version -> {
            this.latestVersion = version;

            int latest = simplifyVersion(version);
            int current = simplifyVersion(plugin.getPluginVersion());

            this.updateAvailable = latest > current;

            if (updateAvailable) {
                plugin.getLog().log(Level.UPDATE, "* New version available! (Current: "
                        + plugin.getPluginVersion()
                        + "), (Latest: "
                        + version
                        + ")");
            } else {
                if (latestVersion == null) return;
                plugin.getLog().log(Level.INFO, "You are running the latest version of VeinGuard! (" + plugin.getPluginVersion() + ")");
            }
        });
    }

    public void retrieveLatestVersion(final Consumer<String> consumer) {
        plugin.getLog().log(Level.INFO, "Checking for updates.");
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, scheduledTask -> {
            try (InputStream inputStream = new URI(latestVersionUrl).toURL().openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.nextLine().trim());
                }
            } catch (IOException | URISyntaxException e) {
                plugin.getLog().log(Level.ERROR, "There was an ERROR while checking for updates!", e);
            }
        });
    }

    private int simplifyVersion(String version) {
        return Integer.parseInt(
                version.replaceAll("[^0-9]", "")
        );
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!announceUpdate || !updateAvailable || latestVersion == null) return;

        Player player = event.getPlayer();
        if (player.hasPermission("veinguard.update") || player.isOp()) {
            String message = plugin.getLocale()
                    .getMessage("new-version", true)
                    .replace("{oldVersion}", plugin.getPluginVersion())
                    .replace("{newVersion}", latestVersion);

            player.sendMessage(message);
        }
    }
}