package com.ferozity.fzcore.core;

import com.ferozity.fzcore.FZcore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    private final FZcore plugin;
    private FileConfiguration coreMessages;

    public ConfigManager(FZcore plugin) {
        this.plugin = plugin;
        loadCoreMessages();
    }

    private void loadCoreMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "core.yml");
        if (messagesFile.exists()) {
            coreMessages = YamlConfiguration.loadConfiguration(messagesFile);
        } else {
            try (InputStream is = plugin.getResource("core.yml")) {
                if (is != null) {
                    coreMessages = YamlConfiguration.loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8));
                } else {
                    coreMessages = new YamlConfiguration();
                }
            } catch (Exception e) {
                coreMessages = new YamlConfiguration();
            }
        }
    }

    public String getCoreMessage(String path, String defaultValue) {
        if (coreMessages == null || !coreMessages.contains(path)) {
            return defaultValue;
        }
        return coreMessages.getString(path, defaultValue);
    }

    public void reloadCoreMessages() {
        loadCoreMessages();
    }

    public FileConfiguration getModuleConfig(String moduleName) {
        File configFile = new File(plugin.getDataFolder(), "configs/" + moduleName + ".yml");
        
        if (!configFile.exists()) {
            return null;
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadAllModuleConfigs() {
        reloadCoreMessages();
    }
}