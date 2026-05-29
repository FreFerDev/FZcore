package com.ferozity.fzcore.commands;

import com.ferozity.fzcore.FZcore;
import com.ferozity.fzcore.core.Module;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.List;

public class FZTabCompleter implements TabCompleter {

    private final FZcore plugin;

    public FZTabCompleter(FZcore plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("fz.admin")) {
            return completions;
        }

        if (args.length == 1) {
            String[] subCommands = {"modules", "enable", "disable", "reload"};
            for (String sub : subCommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable")) {
                for (Module module : plugin.getModuleManager().getLoadedModules().values()) {
                    String name = module.getModuleName();
                    if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(name);
                    }
                }
            }
        }

        return completions;
    }
}