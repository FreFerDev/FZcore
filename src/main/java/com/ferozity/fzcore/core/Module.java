package com.ferozity.fzcore.core;

import org.bukkit.plugin.java.JavaPlugin;

public interface Module {

    void onEnable(JavaPlugin plugin);

    void onDisable();

    String getModuleName();

    String getVersion();

    void reloadConfig();
}