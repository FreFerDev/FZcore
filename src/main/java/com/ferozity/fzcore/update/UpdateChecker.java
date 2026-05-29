package com.ferozity.fzcore.update;

import com.ferozity.fzcore.FZcore;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {

    private final FZcore plugin;
    private final String currentVersion;
    private String latestVersion;
    private boolean updateAvailable;

    public UpdateChecker(FZcore plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.updateAvailable = false;
    }

    public void checkForUpdates(String versionUrl) {
        CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(versionUrl).openStream()))) {
                latestVersion = reader.readLine();
                if (latestVersion == null || latestVersion.isEmpty()) {
                    plugin.getLogger().warning("Could not check for updates: Empty response");
                    return;
                }
                
                updateAvailable = !currentVersion.equals(latestVersion);
                
                if (updateAvailable) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.getLogger().warning("==================================================");
                        plugin.getLogger().warning("A new version of FZcore is available!");
                        plugin.getLogger().warning("Current version: " + currentVersion);
                        plugin.getLogger().warning("Latest version: " + latestVersion);
                        plugin.getLogger().warning("Download: https://github.com/yourusername/FZcore/releases");
                        plugin.getLogger().warning("==================================================");
                    });
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates: " + e.getMessage());
            }
        });
    }

    public void checkForModuleUpdate(String moduleName, String currentVersion, String versionUrl) {
        CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(versionUrl).openStream()))) {
                String latest = reader.readLine();
                if (latest == null || latest.isEmpty()) return;
                
                if (!currentVersion.equals(latest)) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.getLogger().warning("==================================================");
                        plugin.getLogger().warning("A new version of " + moduleName + " is available!");
                        plugin.getLogger().warning("Current version: " + currentVersion);
                        plugin.getLogger().warning("Latest version: " + latest);
                        plugin.getLogger().warning("==================================================");
                    });
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for module update: " + e.getMessage());
            }
        });
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}