package com.ferozity.fzcore.commands;

import com.ferozity.fzcore.FZcore;
import com.ferozity.fzcore.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class FZCommand implements CommandExecutor {

    private final FZcore plugin;

    public FZCommand(FZcore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("fz.admin")) {
                    sender.sendMessage(ColorUtil.colorize(getMessage("commands.no-permission", "&cYou don't have permission to do this.")));
                    return true;
                }
                reloadAll(sender);
                break;

            case "modules":
                if (!sender.hasPermission("fz.admin")) {
                    sender.sendMessage(ColorUtil.colorize(getMessage("commands.no-permission", "&cYou don't have permission to do this.")));
                    return true;
                }
                listModules(sender);
                break;

            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtil.colorize(getMessage("commands.help-header", "&6=== FZcore Help ===")));
        sender.sendMessage(ColorUtil.colorize(getMessage("commands.help-reload", "&e/fz reload &7- Reload all modules")));
        sender.sendMessage(ColorUtil.colorize(getMessage("commands.help-modules", "&e/fz modules &7- List all modules and their status")));
    }

    private void reloadAll(CommandSender sender) {
        plugin.getConfigManager().reloadAllModuleConfigs();
        plugin.getModuleManager().reloadAllModules();
        sender.sendMessage(ColorUtil.colorize(getMessage("commands.reload-success", "&aAll modules have been reloaded successfully!")));
    }

    private void listModules(CommandSender sender) {
        sender.sendMessage(ColorUtil.colorize(getMessage("commands.modules-header", "&6=== FZcore Modules ===")));
        
        Map<String, com.ferozity.fzcore.core.Module> allModules = plugin.getModuleManager().getLoadedModules();
        String enabledStatus = getMessage("commands.module-enabled", "&a✔ Enabled");
        String disabledStatus = getMessage("commands.module-disabled", "&c✘ Disabled");
        String format = getMessage("commands.module-format", "&7- &e{module} &7: {status}");
        
        if (allModules.isEmpty()) {
            sender.sendMessage(ColorUtil.colorize("&7No modules found."));
        } else {
            for (Map.Entry<String, com.ferozity.fzcore.core.Module> entry : allModules.entrySet()) {
                String moduleName = entry.getKey();
                String status = enabledStatus;
                String line = format.replace("{module}", moduleName).replace("{status}", status);
                sender.sendMessage(ColorUtil.colorize(line));
            }
        }
    }
    
    private String getMessage(String path, String defaultValue) {
        return plugin.getConfigManager().getCoreMessage(path, defaultValue);
    }
}