package com.codingcat.catlib.basic;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class PluginDependentClass {
    private final JavaPlugin plugin;
    private final Logger logger;

    public PluginDependentClass(JavaPlugin plugin) {
        if (plugin == null) throw new IllegalArgumentException("Plugin is null!");
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    public JavaPlugin getBasicPlugin() {
        return plugin;
    }
    public <T extends JavaPlugin> T getPlugin() {
        if (this.getBasicPlugin() == null) return null;

        return (T) plugin;
    }

    public Logger getLogger() {
        return logger;
    }
}
