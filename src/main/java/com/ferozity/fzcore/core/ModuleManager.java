package com.ferozity.fzcore.core;

import com.ferozity.fzcore.FZcore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.HandlerList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {

    private final FZcore plugin;
    private final Map<String, Module> loadedModules;
    private final Map<String, String> moduleClassNames;
    private final Map<String, Boolean> previousEnabledState;

    public ModuleManager(FZcore plugin) {
        this.plugin = plugin;
        this.loadedModules = new HashMap<>();
        this.moduleClassNames = new HashMap<>();
        this.previousEnabledState = new HashMap<>();
    }

    public void loadModules() {
        File modulesFile = new File(plugin.getDataFolder(), "modules.yml");
        if (!modulesFile.exists()) {
            plugin.getLogger().warning("modules.yml not found!");
            return;
        }

        FileConfiguration modulesConfig = YamlConfiguration.loadConfiguration(modulesFile);

        if (!modulesConfig.contains("modules")) {
            plugin.getLogger().warning("No 'modules' section found in modules.yml");
            return;
        }

        Map<String, Boolean> currentEnabledState = new HashMap<>();
        int enabledCount = 0;
        
        for (String moduleName : moduleClassNames.keySet()) {
            boolean enabled = modulesConfig.getBoolean("modules." + moduleName + ".enabled", false);
            currentEnabledState.put(moduleName, enabled);
            
            boolean wasEnabled = previousEnabledState.getOrDefault(moduleName, false);
            boolean isEnabled = enabled;
            
            if (wasEnabled && !isEnabled) {
                disableModule(moduleName);
            } else if (!wasEnabled && isEnabled) {
                enableModule(moduleName);
                enabledCount++;
            } else if (wasEnabled && isEnabled) {
                Module module = loadedModules.get(moduleName);
                if (module != null) {
                    module.reloadConfig();
                }
                enabledCount++;
            }
        }
        
        previousEnabledState.clear();
        previousEnabledState.putAll(currentEnabledState);
        
        if (enabledCount == 0) {
            plugin.getLogger().info("No modules to enable");
        }
    }
    
    private void enableModule(String moduleName) {
        if (loadedModules.containsKey(moduleName)) {
            return;
        }
        
        String className = moduleClassNames.get(moduleName);
        if (className == null) {
            plugin.getLogger().warning("Unknown module: " + moduleName);
            return;
        }
        
        try {
            String fullClassName = "com.ferozity.fzcore.modules." + moduleName + "." + className;
            Class<?> moduleClass = Class.forName(fullClassName);
            Module module = (Module) moduleClass.getDeclaredConstructor().newInstance();
            
            module.onEnable(plugin);
            loadedModules.put(moduleName, module);
            plugin.getLogger().info("Enabled: " + moduleName);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to enable module: " + moduleName);
            e.printStackTrace();
        }
    }
    
    private void disableModule(String moduleName) {
        Module module = loadedModules.remove(moduleName);
        if (module != null) {
            try {
                module.onDisable();
                if (module instanceof Listener) {
                    HandlerList.unregisterAll((Listener) module);
                }
                plugin.getLogger().info("Disabled: " + moduleName);
            } catch (Exception e) {
                plugin.getLogger().severe("Error disabling module: " + moduleName);
                e.printStackTrace();
            }
        }
    }
    
    public void reloadAllModules() {
        loadModules();
    }
    
    public void reloadModule(String moduleName) {
        Module module = loadedModules.get(moduleName.toLowerCase());
        if (module != null) {
            try {
                module.reloadConfig();
            } catch (Exception e) {
                plugin.getLogger().severe("Error reloading module: " + moduleName);
                e.printStackTrace();
            }
        }
    }
    
    public void disableModules() {
        List<String> moduleNames = new ArrayList<>(loadedModules.keySet());
        for (String moduleName : moduleNames) {
            disableModule(moduleName);
        }
        loadedModules.clear();
        previousEnabledState.clear();
    }

    public boolean registerExternalModule(Module module) {
        String moduleName = module.getName();
        
        if (loadedModules.containsKey(moduleName)) {
            return false;
        }
        
        try {
            module.onEnable(plugin);
            loadedModules.put(moduleName, module);
            plugin.getLogger().info("Enabled: " + moduleName);
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to enable external module: " + moduleName);
            e.printStackTrace();
            return false;
        }
    }

    public void unregisterExternalModule(String moduleName) {
        Module module = loadedModules.remove(moduleName);
        if (module != null) {
            try {
                module.onDisable();
                plugin.getLogger().info("Disabled: " + moduleName);
            } catch (Exception e) {
                plugin.getLogger().severe("Error disabling external module: " + moduleName);
                e.printStackTrace();
            }
        }
    }

    public Module getModule(String name) {
        return loadedModules.get(name.toLowerCase());
    }

    public boolean isModuleEnabled(String name) {
        return loadedModules.containsKey(name.toLowerCase());
    }

    public Map<String, Module> getLoadedModules() {
        return new HashMap<>(loadedModules);
    }
}