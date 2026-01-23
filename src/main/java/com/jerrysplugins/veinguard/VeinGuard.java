package com.jerrysplugins.veinguard;

import com.jerrysplugins.veinguard.core.command.CommandVeinguard;
import com.jerrysplugins.veinguard.core.common.AlertManager;
import com.jerrysplugins.veinguard.core.common.BlockReport;
import com.jerrysplugins.veinguard.core.common.ConfigOptions;
import com.jerrysplugins.veinguard.core.common.PlayerTracker;
import com.jerrysplugins.veinguard.core.listener.BlockBreakListener;
import com.jerrysplugins.veinguard.core.listener.StaffJoinListener;
import com.jerrysplugins.veinguard.core.util.UpdateChecker;
import com.jerrysplugins.veinguard.core.util.config.ConfigFile;
import com.jerrysplugins.veinguard.core.util.config.LangFile;
import com.jerrysplugins.veinguard.core.util.locale.Locale;
import com.jerrysplugins.veinguard.core.util.logger.Level;
import com.jerrysplugins.veinguard.core.util.logger.Logger;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NonNull;

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
    private final int CONFIG_VERSION = 4;

    @SuppressWarnings("FieldCanBeLocal")
    private final int LANG_VERSION = 4;

    private String pluginName;
    private String pluginVersion;
    private String pluginAuthors;
    private String pluginDescription;
    private String pluginWebsite;

    @Override
    public void onLoad() {
        logger = new Logger(this);
    }

    @Override
    public void onEnable() {
        pluginName = getDescription().getName();
        pluginVersion = getDescription().getVersion();
        pluginAuthors = String.join(", ", getDescription().getAuthors());
        pluginDescription = getDescription().getDescription();
        pluginWebsite = getDescription().getWebsite();

        getLog().log(Level.INFO, "Loading "
                + pluginName + ", v"
                + pluginVersion + ", by "
                + pluginAuthors + ".");

        if(!createConfigs() || !loadCore() || !registerAll()) {
            getLog().log(Level.FATAL, "Critical plugin initialization failure! Plugin is now disabled!");
            getPluginLoader().disablePlugin(this);
            return;
        }

        loadMetrics();

        getLog().log(Level.SUCCESS, "Successfully started "
                + pluginName + ", v"
                + pluginVersion + ", by "
                + pluginAuthors);

        checkForUpdates();
    }

    @Override
    public void onDisable() {
        getLog().log(Level.INFO, "Shutting down "
                + pluginName + ", v"
                + pluginVersion + ", by "
                + pluginAuthors);
        playerTracker.shutdown();
        configOptions.shutdown();
    }

    public boolean reload() {
        getLog().log(Level.DEBUG, "Reloading plugin and configurations.");
        try {
            configFile.reloadConfig();
            langFile.reloadConfig();
            return true;
        } catch (Exception e) {
            getLog().log(Level.ERROR, "There was an error while reloading!", e);
            return false;
        }
    }

    private boolean createConfigs() {
        try {
            getLog().log(Level.DEBUG, "Creating files and configurations.");
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
        getLog().log(Level.DEBUG, "Loading core plugin objects.");
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
        getLog().log(Level.DEBUG, "Registering plugin listeners and commands.");
        try {
            new BlockBreakListener(this);
            new StaffJoinListener(this);
            new CommandVeinguard(this);
            return true;
        } catch (Exception e) {
            getLog().log(Level.ERROR, "There was an error while registering listeners or commands!", e);
            return false;
        }
    }

    private void loadMetrics() {
        getLog().log(Level.DEBUG, "Loading bStats plugin metrics.");
        try {
            new Metrics(this, 28893);
        } catch (Exception e) {
            getLog().log(Level.ERROR, "Failed to load bStats plugin metrics! Metrics will be unavailable!");
        }
    }

    private void checkForUpdates() {
        getLog().log(Level.DEBUG, "Loading plugin update checker.");
        new UpdateChecker(this);
    }

    public Logger getLog() { return this.logger; }
    public Locale getLocale() { return this.locale; }

    @NonNull
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