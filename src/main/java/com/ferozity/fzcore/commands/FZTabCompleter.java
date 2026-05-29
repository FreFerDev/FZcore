package com.ferozity.fzcore.commands;

import com.ferozity.fzcore.FZcore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class FZTabCompleter implements TabCompleter {

    private final List<String> subCommands = List.of("reload", "modules");
    private final FZcore plugin;

    public FZTabCompleter(FZcore plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String subCmd : subCommands) {
                if (subCmd.toLowerCase().startsWith(args[0].toLowerCase())) {
                    if (sender.hasPermission("fz.admin")) {
                        completions.add(subCmd);
                    }
                }
            }
        }

        return completions;
    }
}