package com.ferozity.fzcore;

import com.ferozity.fzcore.commands.FZCommand;
import com.ferozity.fzcore.commands.FZTabCompleter;
import com.ferozity.fzcore.core.ConfigManager;
import com.ferozity.fzcore.core.ModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public final class FZcore extends JavaPlugin {

    private static FZcore instance;
    private ConfigManager configManager;
    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfigs();
        this.configManager = new ConfigManager(this);
        this.moduleManager = new ModuleManager(this);
        
        getCommand("fz").setExecutor(new FZCommand(this));
        getCommand("fz").setTabCompleter(new FZTabCompleter(this));
        
        getLogger().info("=========================");
        moduleManager.loadModules();
        getLogger().info("=========================");
    }

    @Override
    public void onDisable() {
        if (moduleManager != null) {
            moduleManager.disableModules();
        }
    }

    private void saveDefaultConfigs() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveDefaultModuleConfig();
        saveCoreMessages();
        
        File configsDir = new File(getDataFolder(), "configs");
        if (!configsDir.exists()) {
            configsDir.mkdirs();
        }
    }

    private void saveDefaultModuleConfig() {
        File modulesFile = new File(getDataFolder(), "modules.yml");
        if (!modulesFile.exists()) {
            saveResource("modules.yml", false);
        }
    }

    private void saveCoreMessages() {
        File coreMessages = new File(getDataFolder(), "core.yml");
        if (!coreMessages.exists()) {
            saveResource("core.yml", false);
        }
    }

    public static FZcore getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }
}