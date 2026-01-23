package com.jerrysplugins.veinguard.core.hooks;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.util.logger.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public final class DiscordWebhook {

    private static final int TIMEOUT_MS = 10_000;

    private final VeinGuard plugin;
    private URI webhookUri;

    public DiscordWebhook(VeinGuard plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        FileConfiguration config = plugin.getConfig();
        String url = config.getString("discord-webhook-url");
        this.webhookUri = parseWebhookUri(url);
    }

    public boolean isEnabled() {
        return webhookUri != null;
    }

    public void sendDiscordWebhookAsync(
            String playerName,
            String materialName,
            int count,
            long minutes,
            String location
    ) {
        if (!isEnabled()) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                sendPayload(buildEmbedPayload(
                        playerName,
                        materialName,
                        count,
                        minutes,
                        location
                ));
            } catch (Exception e) {
                plugin.getLog().log(Level.WARN,
                        "Failed to send Discord webhook", e);
            }
        });
    }

    private void sendPayload(String jsonPayload) throws Exception {
        HttpURLConnection connection =
                (HttpURLConnection) webhookUri.toURL().openConnection();

        connection.setRequestMethod("POST");
        connection.setConnectTimeout(TIMEOUT_MS);
        connection.setReadTimeout(TIMEOUT_MS);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream out = connection.getOutputStream()) {
            out.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            plugin.getLog().log(Level.WARN,
                    "Discord webhook returned HTTP response code " + responseCode);
        }

        connection.disconnect();
    }

    private String buildEmbedPayload(
            String playerName,
            String materialName,
            int count,
            long minutes,
            String location
    ) {
        String avatarUrl = "https://mc-heads.net/avatar/" + escape(playerName) + "/64";

        return """
        {
          "embeds": [
            {
              "title": "⛏ VeinGuard X-Ray Alert",
              "color": 15158332,
              "timestamp": "%s",
              "thumbnail": { "url": "%s" },
              "fields": [
                { "name": "Player", "value": "%s", "inline": true },
                { "name": "Block", "value": "%s", "inline": true },
                { "name": "Count", "value": "%d in %d min", "inline": true },
                { "name": "Location", "value": "%s", "inline": false }
              ],
              "footer": { "text": "VeinGuard" }
            }
          ]
        }
        """.formatted(
                Instant.now(),
                avatarUrl,
                escape(playerName),
                escape(materialName),
                count,
                minutes,
                escape(location)
        );
    }

    private URI parseWebhookUri(String url) {
        if (url == null || url.isBlank()) return null;

        try {
            return URI.create(url);
        } catch (IllegalArgumentException ex) {
            plugin.getLog().log(Level.ERROR,
                    "Invalid Discord webhook URL in config.yml!");
            return null;
        }
    }

    private String escape(String input) {
        return input == null ? "" :
                input.replace("\\", "\\\\")
                        .replace("\"", "\\\"");
    }
}