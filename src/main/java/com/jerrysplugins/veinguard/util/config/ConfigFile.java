package com.jerrysplugins.veinguard.util.config;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.util.logger.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Set;

public class ConfigFile {

    private final VeinGuard plugin;
    private FileConfiguration config;
    private File configFile;

    public ConfigFile(VeinGuard plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        plugin.getLog().log(Level.DEBUG, "Pushing method reloadConfig() in ConfigFile.class.");
        if(this.configFile == null) { this.configFile = new File(this.plugin.getDataFolder(), "config.yml");}
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream dataStream = this.plugin.getResource("config.yml");
        if(dataStream != null) {
            YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(dataStream));
            this.config.setDefaults(dataConfig);
        }
    }

    public FileConfiguration getConfig() {
        plugin.getLog().log(Level.DEBUG, "Pushing method getConfig() in ConfigFile.class.");
        if (this.config == null) { reloadConfig(); }
        return this.config;
    }

    public void saveConfig() {
        plugin.getLog().log(Level.DEBUG, "Pushing method saveConfig() in ConfigFile.class.");
        if(this.config == null || this.configFile == null) { return; }
        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLog().log(Level.ERROR, "There was an ERROR saving 'config.yml'!");
            plugin.getLog().log(Level.ERROR, e.getMessage());
        }
    }

    public void saveDefaultConfig() {
        plugin.getLog().log(Level.DEBUG, "Pushing method saveDefaultConfig() in ConfigFile.class.");
        if (this.configFile == null) {
            this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
        }

        if (!this.configFile.exists()) {
            this.plugin.saveResource("config.yml", false);
            reloadConfig();

            getConfig().set("config-version", plugin.getConfigVersion());
            saveConfig();
        }
    }

    public void checkUpdateConfig() {
        plugin.getLog().log(Level.DEBUG, "Pushing method checkUpdateConfig() in ConfigFile.class.");
        reloadConfig();
        int currentVersion = config.getInt("config-version", 1);
        if (currentVersion >= plugin.getConfigVersion()) return;

        plugin.getLog().log(Level.WARN, "Old config.yml detected (version "
                + currentVersion + "), upgrading to version "
                + plugin.getConfigVersion() + ".");

        File backup = new File(plugin.getDataFolder(), "config-old.yml");
        boolean renamed = configFile.renameTo(backup);
        if (!renamed) {
            plugin.getLog().log(Level.ERROR, "Failed to backup old config.yml. Make sure the file is not open or locked.");
            return;
        }

        Set<String> keys = config.getKeys(true);
        YamlConfiguration oldValues = new YamlConfiguration();
        for (String key : keys) {
            oldValues.set(key, config.get(key));
        }

        plugin.saveResource("config.yml", true);
        reloadConfig();

        for (String key : keys) {
            if (config.contains(key)) {
                config.set(key, oldValues.get(key));
            }
        }

        config.set("config-version", plugin.getConfigVersion());

        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLog().log(Level.ERROR, "Failed to save updated config.yml", e);
            return;
        }

        plugin.getLog().log(Level.SUCCESS, "Config updated successfully. Old config saved as config-old.yml.");
    }
}