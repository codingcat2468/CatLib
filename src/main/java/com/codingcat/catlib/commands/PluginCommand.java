package com.codingcat.catlib.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public interface PluginCommand extends CommandExecutor, TabCompleter {
    String getName();
}
