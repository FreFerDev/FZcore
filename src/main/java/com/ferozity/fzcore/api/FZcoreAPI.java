package com.ferozity.fzcore.api;

import com.ferozity.fzcore.FZcore;
import com.ferozity.fzcore.core.Module;
import com.ferozity.fzcore.utils.ActionBarUtil;
import com.ferozity.fzcore.utils.ColorUtil;
import com.ferozity.fzcore.utils.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.CommandExecutor;
import org.bukkit.scheduler.BukkitTask;
import java.io.File;

public class FZcoreAPI {

    private final FZcore plugin;

    public FZcoreAPI(FZcore plugin) {
        this.plugin = plugin;
    }

    public static FZcoreAPI getInstance() {
        return new FZcoreAPI(FZcore.getInstance());
    }

    public void sendActionBar(Player player, String message) {
        ActionBarUtil.send(player, message);
    }

    public void playSound(Player player, org.bukkit.Sound sound, float volume, float pitch) {
        SoundUtil.playSound(player, sound, volume, pitch);
    }

    public String colorize(String message) {
        return ColorUtil.colorize(message);
    }

    public boolean registerExternalModule(Module module) {
        return plugin.getModuleManager().registerModule(module);
    }

    public void unregisterExternalModule(String moduleName) {
        plugin.getModuleManager().unregisterModule(moduleName);
    }

    public boolean isModuleEnabled(String moduleName) {
        return plugin.getModuleManager().isModuleEnabled(moduleName);
    }

    public Module getModule(String moduleName) {
        return plugin.getModuleManager().getModule(moduleName);
    }

    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    public void reloadAllModules() {
        plugin.getModuleManager().reloadAllModules();
    }
    
    public FileConfiguration getModuleConfig(String moduleName) {
        return plugin.getConfigManager().getModuleConfig(moduleName);
    }
    
    public File getCoreFolder() {
        return plugin.getConfigManager().getCoreFolder();
    }
    
    public File getConfigsFolder() {
        return plugin.getConfigManager().getConfigsFolder();
    }
    
    public void saveModuleConfig(String moduleName, FileConfiguration config) {
        plugin.getConfigManager().saveModuleConfig(moduleName, config);
    }
    
    public void registerCommand(String moduleName, String commandName, CommandExecutor executor) {
        plugin.getModuleManager().registerModuleCommand(moduleName, commandName, executor);
    }
    
    public BukkitTask runTaskTimer(String moduleName, Runnable task, long delay, long period) {
        return plugin.getModuleManager().runTaskTimer(moduleName, task, delay, period);
    }
    
    public void cancelTasks(String moduleName) {
        plugin.getModuleManager().cancelAllModuleTasks(moduleName);
    }
}