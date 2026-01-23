package com.jerrysplugins.veinguard.core.util.config;

import com.jerrysplugins.veinguard.VeinGuard;
import com.jerrysplugins.veinguard.core.util.logger.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

public class LangFile {

    private final VeinGuard plugin;
    private FileConfiguration langConfig;
    private File langFile;

    public LangFile(VeinGuard plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if(this.langFile == null) { this.langFile = new File(this.plugin.getDataFolder(), "lang.yml");}
        this.langConfig = YamlConfiguration.loadConfiguration(this.langFile);
        InputStream dataStream = this.plugin.getResource("lang.yml");
        if(dataStream != null) {
            YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(dataStream));
            this.langConfig.setDefaults(dataConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.langConfig == null) { reloadConfig(); }
        return this.langConfig;
    }

    public void saveConfig() {
        if(this.langConfig == null || this.langFile == null) { return; }
        try {
            this.getConfig().save(this.langFile);
            plugin.getLog().log(Level.DEBUG, "File 'lang.yml' has been saved!");
        } catch (IOException e) {
            plugin.getLog().log(Level.ERROR, "There was an ERROR saving 'lang.yml'!");
            plugin.getLog().log(Level.ERROR, e.getMessage());
        }
    }

    public void saveDefaultConfig() {
        if (this.langFile == null) {
            this.langFile = new File(this.plugin.getDataFolder(), "lang.yml");
        }

        if (!this.langFile.exists()) {
            this.plugin.saveResource("lang.yml", false);
            reloadConfig();

            getConfig().set("lang-version", plugin.getLangVersion());
            saveConfig();
        }
    }

    public void checkUpdateConfig() {
        reloadConfig();
        int currentVersion = langConfig.getInt("lang-version", 1);
        if (currentVersion >= plugin.getLangVersion()) return;

        plugin.getLog().log(Level.WARN, "Old lang.yml detected (version "
                + currentVersion + "), upgrading to version "
                + plugin.getLangVersion() + ".");

        File backup = new File(plugin.getDataFolder(), "lang-old.yml");
        boolean renamed = langFile.renameTo(backup);
        if (!renamed) {
            plugin.getLog().log(Level.ERROR, "Failed to backup old lang.yml. Make sure the file is not open or locked.");
            return;
        }

        Set<String> keys = langConfig.getKeys(true);
        YamlConfiguration oldValues = new YamlConfiguration();
        for (String key : keys) {
            oldValues.set(key, langConfig.get(key));
        }

        plugin.saveResource("lang.yml", true);
        reloadConfig();

        for (String key : keys) {
            if (langConfig.contains(key)) {
                langConfig.set(key, oldValues.get(key));
            }
        }

        langConfig.set("lang-version", plugin.getLangVersion());

        try {
            langConfig.save(langFile);
        } catch (IOException e) {
            plugin.getLog().log(Level.ERROR, "Failed to save updated lang.yml", e);
            return;
        }

        plugin.getLog().log(Level.SUCCESS, "Lang updated successfully. Old lang saved as lang-old.yml.");
    }
}