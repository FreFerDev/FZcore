package com.ferozity.fzcore.core;

import com.ferozity.fzcore.FZcore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public class ConfigManager {

    private final FZcore plugin;

    public ConfigManager(FZcore plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getModuleConfig(String moduleName) {
        File configFile = new File(plugin.getDataFolder(), "configs/" + moduleName + ".yml");
        
        if (!configFile.exists()) {
            return null;
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }

    public File getCoreFolder() {
        return plugin.getDataFolder();
    }
    
    public File getConfigsFolder() {
        File configsDir = new File(plugin.getDataFolder(), "configs");
        if (!configsDir.exists()) {
            configsDir.mkdirs();
        }
        return configsDir;
    }
    
    public void saveModuleConfig(String moduleName, FileConfiguration config) {
        File configFile = new File(plugin.getDataFolder(), "configs/" + moduleName + ".yml");
        try {
            config.save(configFile);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save config for module: " + moduleName);
        }
    }
}