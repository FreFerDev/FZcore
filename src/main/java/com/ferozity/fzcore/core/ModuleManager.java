package com.ferozity.fzcore.core;

import com.ferozity.fzcore.FZcore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleManager {

    private final FZcore plugin;
    private final Map<String, Module> loadedModules;
    private final Map<String, Boolean> moduleStates;
    private final Map<String, List<BukkitTask>> moduleTasks;
    private final Map<String, List<String>> moduleCommands;
    private final File stateFile;

    public ModuleManager(FZcore plugin) {
        this.plugin = plugin;
        this.loadedModules = new ConcurrentHashMap<>();
        this.moduleStates = new ConcurrentHashMap<>();
        this.moduleTasks = new ConcurrentHashMap<>();
        this.moduleCommands = new ConcurrentHashMap<>();
        this.stateFile = new File(plugin.getDataFolder(), "modules.dat");
        loadState();
    }

    public boolean registerModule(Module module) {
        String name = module.getModuleName();
        
        if (loadedModules.containsKey(name)) {
            plugin.getLogger().warning("Module '" + name + "' already registered!");
            return false;
        }
        
        boolean enabled = moduleStates.getOrDefault(name, true);
        
        if (enabled) {
            try {
                module.onEnable(plugin);
                loadedModules.put(name, module);
                moduleStates.put(name, true);
                plugin.getLogger().info("Module enabled: " + name + " v" + module.getVersion());
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to enable module: " + name);
                e.printStackTrace();
                return false;
            }
        } else {
            loadedModules.put(name, module);
            plugin.getLogger().info("Module registered (disabled): " + name + " v" + module.getVersion());
        }
        
        saveState();
        return true;
    }

    public void unregisterModule(String moduleName) {
        Module module = loadedModules.remove(moduleName);
        if (module != null) {
            try {
                if (moduleStates.getOrDefault(moduleName, false)) {
                    module.onDisable();
                    if (module instanceof Listener) {
                        HandlerList.unregisterAll((Listener) module);
                    }
                }
                cancelAllModuleTasks(moduleName);
                unregisterAllModuleCommands(moduleName);
            } catch (Exception e) {
                plugin.getLogger().severe("Error disabling module: " + moduleName);
                e.printStackTrace();
            }
        }
        moduleStates.remove(moduleName);
        saveState();
    }

    public boolean enableModule(String moduleName) {
        Module module = loadedModules.get(moduleName);
        if (module == null) {
            return false;
        }
        
        if (moduleStates.getOrDefault(moduleName, false)) {
            return true;
        }
        
        try {
            module.onEnable(plugin);
            moduleStates.put(moduleName, true);
            if (module instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) module, plugin);
            }
            plugin.getLogger().info("Module enabled: " + moduleName);
            saveState();
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to enable module: " + moduleName);
            e.printStackTrace();
            return false;
        }
    }

    public void disableModule(String moduleName) {
        Module module = loadedModules.get(moduleName);
        if (module == null) {
            return;
        }
        
        if (!moduleStates.getOrDefault(moduleName, false)) {
            return;
        }
        
        try {
            module.onDisable();
            if (module instanceof Listener) {
                HandlerList.unregisterAll((Listener) module);
            }
            cancelAllModuleTasks(moduleName);
            moduleStates.put(moduleName, false);
            plugin.getLogger().info("Module disabled: " + moduleName);
            saveState();
        } catch (Exception e) {
            plugin.getLogger().severe("Error disabling module: " + moduleName);
            e.printStackTrace();
        }
    }

    public void reloadAllModules() {
        for (Map.Entry<String, Module> entry : loadedModules.entrySet()) {
            String name = entry.getKey();
            Module module = entry.getValue();
            boolean enabled = moduleStates.getOrDefault(name, false);
            
            if (enabled) {
                try {
                    module.reloadConfig();
                } catch (Exception e) {
                    plugin.getLogger().severe("Error reloading module: " + name);
                    e.printStackTrace();
                }
            }
        }
        plugin.getLogger().info("All modules reloaded");
    }

    public void disableAllModules() {
        for (Map.Entry<String, Module> entry : loadedModules.entrySet()) {
            String name = entry.getKey();
            Module module = entry.getValue();
            boolean enabled = moduleStates.getOrDefault(name, false);
            
            if (enabled) {
                try {
                    module.onDisable();
                    if (module instanceof Listener) {
                        HandlerList.unregisterAll((Listener) module);
                    }
                    cancelAllModuleTasks(name);
                } catch (Exception e) {
                    plugin.getLogger().severe("Error disabling module: " + name);
                    e.printStackTrace();
                }
            }
        }
        loadedModules.clear();
    }

    public void loadModules() {
        plugin.getLogger().info("Loading modules...");
        int enabledCount = 0;
        
        for (Map.Entry<String, Boolean> entry : moduleStates.entrySet()) {
            if (entry.getValue()) {
                enabledCount++;
            }
        }
        
        plugin.getLogger().info("Loaded " + loadedModules.size() + " modules (" + enabledCount + " active)");
    }

    public void registerModuleCommand(String moduleName, String commandName, CommandExecutor executor) {
        moduleCommands.computeIfAbsent(moduleName, k -> new ArrayList<>()).add(commandName);
        PluginCommand cmd = plugin.getCommand(commandName);
        if (cmd != null) {
            cmd.setExecutor(executor);
        }
    }

    private void unregisterAllModuleCommands(String moduleName) {
        List<String> commands = moduleCommands.remove(moduleName);
        if (commands != null) {
            for (String cmdName : commands) {
                PluginCommand cmd = plugin.getCommand(cmdName);
                if (cmd != null) {
                    cmd.setExecutor(null);
                }
            }
        }
    }

    public BukkitTask runTaskTimer(String moduleName, Runnable task, long delay, long period) {
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, task, delay, period);
        moduleTasks.computeIfAbsent(moduleName, k -> new ArrayList<>()).add(bukkitTask);
        return bukkitTask;
    }

    public void cancelAllModuleTasks(String moduleName) {
        List<BukkitTask> tasks = moduleTasks.remove(moduleName);
        if (tasks != null) {
            for (BukkitTask task : tasks) {
                if (task != null && !task.isCancelled()) {
                    task.cancel();
                }
            }
        }
    }

    public Module getModule(String name) {
        return loadedModules.get(name.toLowerCase());
    }

    public boolean isModuleEnabled(String name) {
        return moduleStates.getOrDefault(name.toLowerCase(), false);
    }

    public Map<String, Module> getLoadedModules() {
        return new HashMap<>(loadedModules);
    }

    public Map<String, Boolean> getAllModuleStates() {
        return new HashMap<>(moduleStates);
    }

    public void saveState() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(stateFile))) {
            out.writeInt(moduleStates.size());
            for (Map.Entry<String, Boolean> entry : moduleStates.entrySet()) {
                out.writeUTF(entry.getKey());
                out.writeBoolean(entry.getValue());
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save module states: " + e.getMessage());
        }
    }

    private void loadState() {
        if (!stateFile.exists()) {
            return;
        }
        
        try (DataInputStream in = new DataInputStream(new FileInputStream(stateFile))) {
            int size = in.readInt();
            for (int i = 0; i < size; i++) {
                String name = in.readUTF();
                boolean enabled = in.readBoolean();
                moduleStates.put(name, enabled);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to load module states: " + e.getMessage());
        }
    }
}