package com.ferozity.fzcore.commands;

import com.ferozity.fzcore.FZcore;
import com.ferozity.fzcore.core.Module;
import com.ferozity.fzcore.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
            case "modules":
            case "mods":
                if (!sender.hasPermission("fz.admin")) {
                    sender.sendMessage(ColorUtil.colorize("&cYou don't have permission to do this."));
                    return true;
                }
                listModules(sender);
                break;
                
            case "enable":
                if (!sender.hasPermission("fz.admin")) {
                    sender.sendMessage(ColorUtil.colorize("&cYou don't have permission to do this."));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.colorize("&cUsage: /fz enable <module>"));
                    return true;
                }
                enableModule(sender, args[1]);
                break;
                
            case "disable":
                if (!sender.hasPermission("fz.admin")) {
                    sender.sendMessage(ColorUtil.colorize("&cYou don't have permission to do this."));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ColorUtil.colorize("&cUsage: /fz disable <module>"));
                    return true;
                }
                disableModule(sender, args[1]);
                break;
                
            case "reload":
                if (!sender.hasPermission("fz.admin")) {
                    sender.sendMessage(ColorUtil.colorize("&cYou don't have permission to do this."));
                    return true;
                }
                reloadAll(sender);
                break;
                
            default:
                sendHelp(sender);
                break;
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ColorUtil.colorize("&6=== FZcore Help ==="));
        sender.sendMessage(ColorUtil.colorize("&e/fz modules &7- List all modules and their status"));
        sender.sendMessage(ColorUtil.colorize("&e/fz enable <module> &7- Enable a module"));
        sender.sendMessage(ColorUtil.colorize("&e/fz disable <module> &7- Disable a module"));
        sender.sendMessage(ColorUtil.colorize("&e/fz reload &7- Reload all modules"));
    }

    private void listModules(CommandSender sender) {
        Map<String, Module> modules = plugin.getModuleManager().getLoadedModules();
        Map<String, Boolean> states = plugin.getModuleManager().getAllModuleStates();
        
        if (modules.isEmpty()) {
            sender.sendMessage(ColorUtil.colorize("&7No modules found."));
            return;
        }
        
        sender.sendMessage(ColorUtil.colorize("&6=== FZcore Modules ==="));
        
        for (Map.Entry<String, Module> entry : modules.entrySet()) {
            String name = entry.getKey();
            Module module = entry.getValue();
            boolean enabled = states.getOrDefault(name, false);
            
            String status;
            if (enabled) {
                status = ColorUtil.colorize("&a✔ ACTIVE");
            } else {
                status = ColorUtil.colorize("&c✘ DISABLED");
            }
            
            sender.sendMessage(ColorUtil.colorize("&7- &e" + name + " &7v" + module.getVersion() + " &7: " + status));
        }
    }

    private void enableModule(CommandSender sender, String moduleName) {
        Module module = plugin.getModuleManager().getModule(moduleName);
        
        if (module == null) {
            sender.sendMessage(ColorUtil.colorize("&cModule '" + moduleName + "' not found!"));
            return;
        }
        
        if (plugin.getModuleManager().isModuleEnabled(moduleName)) {
            sender.sendMessage(ColorUtil.colorize("&cModule '" + moduleName + "' is already enabled!"));
            return;
        }
        
        if (plugin.getModuleManager().enableModule(moduleName)) {
            sender.sendMessage(ColorUtil.colorize("&aModule '" + moduleName + "' has been enabled!"));
        } else {
            sender.sendMessage(ColorUtil.colorize("&cFailed to enable module '" + moduleName + "'! Check console."));
        }
    }

    private void disableModule(CommandSender sender, String moduleName) {
        Module module = plugin.getModuleManager().getModule(moduleName);
        
        if (module == null) {
            sender.sendMessage(ColorUtil.colorize("&cModule '" + moduleName + "' not found!"));
            return;
        }
        
        if (!plugin.getModuleManager().isModuleEnabled(moduleName)) {
            sender.sendMessage(ColorUtil.colorize("&cModule '" + moduleName + "' is already disabled!"));
            return;
        }
        
        plugin.getModuleManager().disableModule(moduleName);
        sender.sendMessage(ColorUtil.colorize("&cModule '" + moduleName + "' has been disabled!"));
    }

    private void reloadAll(CommandSender sender) {
        plugin.getModuleManager().reloadAllModules();
        sender.sendMessage(ColorUtil.colorize("&aAll modules have been reloaded!"));
    }
}