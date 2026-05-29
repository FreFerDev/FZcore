package com.ferozity.fzcore;

import com.ferozity.fzcore.commands.FZCommand;
import com.ferozity.fzcore.commands.FZTabCompleter;
import com.ferozity.fzcore.core.ConfigManager;
import com.ferozity.fzcore.core.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public final class FZcore extends JavaPlugin {

    private static FZcore instance;
    private ConfigManager configManager;
    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        instance = this;
        
        createDataFolder();
        
        this.configManager = new ConfigManager(this);
        this.moduleManager = new ModuleManager(this);
        
        getCommand("fz").setExecutor(new FZCommand(this));
        getCommand("fz").setTabCompleter(new FZTabCompleter(this));
        
        getLogger().info("=========================");
        getLogger().info("FZcore v" + getDescription().getVersion() + " enabled");
        
        moduleManager.loadModules();
        
        getLogger().info("=========================");
    }

    @Override
    public void onDisable() {
        if (moduleManager != null) {
            moduleManager.disableAllModules();
            moduleManager.saveState();
        }
        getLogger().info("FZcore disabled");
    }
    
    private void createDataFolder() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        File configsDir = new File(getDataFolder(), "configs");
        if (!configsDir.exists()) {
            configsDir.mkdirs();
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