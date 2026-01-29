package com.jerrysplugins.veinguard;

import com.jerrysplugins.veinguard.command.CommandManager;
import com.jerrysplugins.veinguard.core.AlertManager;
import com.jerrysplugins.veinguard.core.BlockReport;
import com.jerrysplugins.veinguard.core.ConfigOptions;
import com.jerrysplugins.veinguard.core.PlayerTracker;
import com.jerrysplugins.veinguard.listener.BlockBreakListener;
import com.jerrysplugins.veinguard.listener.StaffJoinListener;
import com.jerrysplugins.veinguard.util.config.ConfigFile;
import com.jerrysplugins.veinguard.util.config.LangFile;
import com.jerrysplugins.veinguard.util.locale.Locale;
import com.jerrysplugins.veinguard.util.logger.Level;
import com.jerrysplugins.veinguard.util.logger.Logger;
import com.jerrysplugins.veinguard.util.update.UpdateChecker;
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
    private BlockReport blockReport;
    @SuppressWarnings("FieldCanBeLocal")
    private final int CONFIG_VERSION = 5;

    @SuppressWarnings("FieldCanBeLocal")
    private final int LANG_VERSION = 5;

    private String pluginName;
    private String pluginVersion;
    private String pluginAuthors;
    private String pluginDescription;
    private String pluginWebsite;

    @Override
    public void onLoad() {
        logger = new Logger(this);

        if(!createConfigs()) {
            getLog().log(Level.FATAL, "Critical plugin initialization failure! Plugin is now disabled!");
            getPluginLoader().disablePlugin(this);
            return;
        }

        if(logger.isDebugConfigEnabled()) logger.setDebugEnabled(true);
    }

    @Override
    public void onEnable() {

        checkForUpdates();

        pluginName = getDescription().getName();
        pluginVersion = getDescription().getVersion();
        pluginAuthors = String.join(", ", getDescription().getAuthors());
        pluginDescription = getDescription().getDescription();
        pluginWebsite = getDescription().getWebsite();

        getLog().log(Level.INFO, "Loading "
                + pluginName + ", v"
                + pluginVersion + ", by "
                + pluginAuthors + ".");

        if(!loadCore() || !registerAll()) {
            getLog().log(Level.FATAL, "Critical plugin initialization failure! Plugin is now disabled!");
            getPluginLoader().disablePlugin(this);
            return;
        }

        loadMetrics();

        getLog().log(Level.SUCCESS, "Successfully enabled "
                + pluginName + ", v"
                + pluginVersion + ", by "
                + pluginAuthors);
    }

    @Override
    public void onDisable() {
        playerTracker.shutdown();
        configOptions.shutdown();
        getLog().log(Level.INFO, "Plugin disabled. Goodbye!");
    }

    public boolean reload() {
        getLog().log(Level.DEBUG, "Pushing method reload() in VeinGuard.class.");
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
            getLog().log(Level.DEBUG, "Pushing method createConfigs() in VeinGuard.class.");
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
        getLog().log(Level.DEBUG, "Pushing method loadCore() in VeinGuard.class.");
        try {
            locale = new Locale(this);
            configOptions = new ConfigOptions(this);
            playerTracker = new PlayerTracker(this);
            alertManager = new AlertManager(this);
            blockReport = new BlockReport(this);
            return true;
        } catch (Exception e) {
            getLog().log(Level.ERROR, "There was an error while loading core plugin objects!", e);
            return false;
        }
    }

    private boolean registerAll() {
        getLog().log(Level.DEBUG, "Pushing method registerAll() in VeinGuard.class.");
        try {
            new BlockBreakListener(this);
            new StaffJoinListener(this);
            new CommandManager(this);
            return true;
        } catch (Exception e) {
            getLog().log(Level.ERROR, "There was an error while registering listeners or commands!", e);
            return false;
        }
    }

    private void loadMetrics() {
        getLog().log(Level.DEBUG, "Pushing method loadMetrics() in VeinGuard.class.");
        try {
            new Metrics(this, 28893);
        } catch (Exception e) {
            getLog().log(Level.ERROR, "Failed to load bStats plugin metrics! Metrics will be unavailable!");
        }
    }

    private void checkForUpdates() {
        getLog().log(Level.DEBUG, "Pushing method checkForUpdates() in VeinGuard.class.");
        new UpdateChecker(this);
    }

    public Logger getLog() { return this.logger; }
    public Locale getLocale() { return this.locale; }

    public FileConfiguration getConfig() { return this.configFile.getConfig(); }
    public FileConfiguration getLangConfig() { return this.langFile.getConfig(); }

    public ConfigOptions getConfigOptions() { return this.configOptions; }
    public PlayerTracker getPlayerTracker() { return this.playerTracker; }
    public AlertManager getAlertManager() { return this.alertManager; }
    public BlockReport getBlockReport() { return this.blockReport; }

    public String getPluginVersion() { return this.pluginVersion; }
    public String getPluginAuthors() { return this.pluginAuthors; }
    public String getPluginDescription() { return this.pluginDescription; }
    public String getPluginWebsite() { return this.pluginWebsite; }

    public int getConfigVersion() { return this.CONFIG_VERSION; }
    public int getLangVersion() { return this.LANG_VERSION; }
}