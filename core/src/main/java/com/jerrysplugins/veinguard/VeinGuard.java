/*
 * Copyright (c) 2026 JerrysPlugins
 * SPDX‑License‑Identifier: MIT
 * Licensed under the MIT License (see LICENSE file)
 * DO NOT REMOVE: This header must remain in all source files.
 */
package com.jerrysplugins.veinguard;

import com.jerrysplugins.veinguard.command.CommandManager;
import com.jerrysplugins.veinguard.common.alert.AlertManager;
import com.jerrysplugins.veinguard.common.ConfigOptions;
import com.jerrysplugins.veinguard.common.PlayerTracker;
import com.jerrysplugins.veinguard.listener.VGListener;
import com.jerrysplugins.veinguard.common.patrol.PatrolManager;
import com.jerrysplugins.veinguard.detection.DetectionListener;
import com.jerrysplugins.veinguard.detection.VeinTracker;
import com.jerrysplugins.veinguard.detection.XRayDetector;
import com.jerrysplugins.veinguard.integration.HookManager;
import com.jerrysplugins.veinguard.util.config.ConfigFile;
import com.jerrysplugins.veinguard.util.config.LangFile;
import com.jerrysplugins.veinguard.util.locale.Locale;
import com.jerrysplugins.veinguard.util.logger.Level;
import com.jerrysplugins.veinguard.util.logger.Logger;
import com.jerrysplugins.veinguard.util.version.UpdateService;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class VeinGuard extends JavaPlugin {

    private Logger logger;
    private Locale locale;

    private ConfigFile configFile;
    private LangFile langFile;

    private ConfigOptions configOptions;
    private PlayerTracker playerTracker;
    private AlertManager alertManager;
    private PatrolManager patrolManager;
    private HookManager hookManager;
    private CommandManager commandManager;

    private XRayDetector xRayDetector;
    private VeinTracker veinTracker;

    private UpdateService updateService;

    @SuppressWarnings("FieldCanBeLocal")
    private final int CONFIG_VERSION = 7;

    @SuppressWarnings("FieldCanBeLocal")
    private final int LANG_VERSION = 7;

    private String pluginName;
    private String pluginVersion;
    private String pluginAuthors;
    private String pluginDescription;
    private String pluginWebsite;

    @Override
    public void onLoad() {

        logger = new Logger(this);

        pluginName = getDescription().getName();
        pluginVersion = getDescription().getVersion();
        pluginAuthors = String.join(", ", getDescription().getAuthors());
        pluginDescription = getDescription().getDescription();
        pluginWebsite = getDescription().getWebsite();

        getLog().log(Level.INFO, "Loading "
                + pluginName + ", v"
                + pluginVersion + ", by "
                + pluginAuthors + ".");

        if(!createConfigs()) {
            getLog().log(Level.FATAL, "Critical plugin initialization failure! Plugin is now disabled!");
            getPluginLoader().disablePlugin(this);
            return;
        }

        if(logger.isDebugConfigEnabled()) logger.setDebugEnabled(true);

        locale = new Locale(this);
        configOptions = new ConfigOptions(this);

        hookManager = new HookManager(this);
        hookManager.onLoad();
    }

    @Override
    public void onEnable() {

        if(!loadCore() || !register()) {
            getLog().log(Level.FATAL, "Critical plugin initialization failure! Plugin is now disabled!");
            getPluginLoader().disablePlugin(this);
            return;
        }

        loadMetrics();

        getLog().log(Level.SUCCESS, "Successfully enabled "
                + pluginName + ", v"
                + pluginVersion + ", by "
                + pluginAuthors);

        checkForUpdates();
    }

    @Override
    public void onDisable() {
        if (patrolManager != null) {
            patrolManager.shutdown();
        }
        if (hookManager != null) {
            hookManager.shutdown();
        }
        getAlertManager().getActionBarQueue().shutdown();
        playerTracker.shutdown();
        configOptions.shutdown();
        getLog().log(Level.INFO, "Plugin disabled. Goodbye!");
    }

    public boolean reload() {
        try {
            configFile.reloadConfig();
            langFile.reloadConfig();
            alertManager.getDiscordWebhook().reload();
            configOptions.reload();
            return true;
        } catch (Exception e) {
            getLog().log(Level.ERROR, "There was an error while reloading!", e);
            return false;
        }
    }

    private boolean createConfigs() {
        try {
            configFile = new ConfigFile(this);
            langFile = new LangFile(this);
            configFile.checkUpdateConfig();
            langFile.checkUpdateConfig();
            return true;
        } catch (Exception e) {
            getLog().log(Level.ERROR, "There was an error while creating plugin configs!", e);
            return false;
        }
    }

    private boolean loadCore() {
        try {
            playerTracker = new PlayerTracker(this);
            alertManager = new AlertManager(this);
            patrolManager = new PatrolManager(this);

            veinTracker = new VeinTracker();
            xRayDetector = new XRayDetector(veinTracker);

            hookManager.onEnable();
            return true;
        } catch (Exception e) {
            getLog().log(Level.ERROR, "There was an error while loading core plugin objects!", e);
            return false;
        }
    }

    private boolean register() {
        try {
            new VGListener(this);
            new DetectionListener(this);
            commandManager = new CommandManager(this);
            return true;
        } catch (Exception e) {
            getLog().log(Level.ERROR, "There was an error while registering listeners or commands!", e);
            return false;
        }
    }

    private void loadMetrics() {
        try {
            new Metrics(this, 28893);
        } catch (Exception e) {
            getLog().log(Level.ERROR, "Failed to load bStats plugin metrics! Metrics will be unavailable!");
        }
    }

    private void checkForUpdates() {
        updateService = new UpdateService(this);
    }

    public Logger getLog() { return this.logger; }
    public Locale getLocale() { return this.locale; }

    public ConfigFile getConfigFile() { return this.configFile; }
    public FileConfiguration getVGConfig() { return this.configFile.getConfig(); }
    public FileConfiguration getLangConfig() { return this.langFile.getConfig(); }

    public ConfigOptions getConfigOptions() { return this.configOptions; }
    public PlayerTracker getPlayerTracker() { return this.playerTracker; }
    public AlertManager getAlertManager() { return this.alertManager; }
    public PatrolManager getPatrolManager() { return this.patrolManager; }
    public HookManager getHookManager() { return this.hookManager; }
    public CommandManager getCommandManager() { return this.commandManager; }

    public XRayDetector getXRayDetector() { return this.xRayDetector; }
    public VeinTracker getVeinTracker() { return this.veinTracker; }

    public UpdateService getUpdateService() { return this.updateService; }

    public String getPluginVersion() { return this.pluginVersion; }
    public String getPluginAuthors() { return this.pluginAuthors; }
    public String getPluginDescription() { return this.pluginDescription; }
    public String getPluginWebsite() { return this.pluginWebsite; }

    public int getConfigVersion() { return this.CONFIG_VERSION; }
    public int getLangVersion() { return this.LANG_VERSION; }
}