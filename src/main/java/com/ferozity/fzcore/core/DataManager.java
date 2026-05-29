package com.ferozity.fzcore.core;

import com.ferozity.fzcore.FZcore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final FZcore plugin;
    private final Map<String, Map<UUID, Object>> dataStorage;

    public DataManager(FZcore plugin) {
        this.plugin = plugin;
        this.dataStorage = new HashMap<>();
        createDataFolder();
    }

    private void createDataFolder() {
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public File getDataFolder() {
        return new File(plugin.getDataFolder(), "data");
    }

    public void saveData(String moduleName, UUID playerId, Object data) {
        dataStorage.computeIfAbsent(moduleName, k -> new HashMap<>()).put(playerId, data);
    }

    public Object getData(String moduleName, UUID playerId) {
        Map<UUID, Object> moduleData = dataStorage.get(moduleName);
        if (moduleData != null) {
            return moduleData.get(playerId);
        }
        return null;
    }

    public void clearModuleData(String moduleName) {
        dataStorage.remove(moduleName);
    }

    public void clearAllData() {
        dataStorage.clear();
    }
}