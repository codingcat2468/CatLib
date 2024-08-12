package com.codingcat.catlib.commands;

import com.codingcat.catlib.basic.PluginDependentClass;
import com.codingcat.catlib.util.ReflectUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.util.Set;

public class CommandLoader extends PluginDependentClass {
    private final static String PLUGIN_COMMANDS_PACKAGE = ".commands";
    private final String packageName;
    public CommandLoader(String packageName, JavaPlugin plugin) {
        super(plugin);
        this.packageName = packageName;
    }

    public void initAll(Class<? extends JavaPlugin> pluginClass) {
        String packageName = this.packageName + CommandLoader.PLUGIN_COMMANDS_PACKAGE;

        Set<? extends Class<? extends PluginCommand>> commandClasses = ReflectUtil.getClassesImplementingOrExtending(packageName, PluginCommand.class);
        for (Class<? extends PluginCommand> commandClass : commandClasses) {
            try {
                PluginCommand command;
                if (commandClass.getSuperclass() == PluginDependentClass.class) {
                    Constructor<? extends PluginCommand> pluginDependentConstructor = commandClass.getConstructor(pluginClass);
                    command = pluginDependentConstructor.newInstance(getPlugin());
                } else {
                    Constructor<? extends PluginCommand> constructor = commandClass.getConstructor();
                    command = constructor.newInstance();
                }

                this.registerBukkitCommand(command);
            } catch (ReflectiveOperationException exception) {
                getLogger().warning(String.format("Cannot register command class %s: %s", commandClass.getName(), exception));
            }
        }
    }

    private void registerBukkitCommand(PluginCommand command) {
        org.bukkit.command.PluginCommand pluginCommand = getPlugin().getCommand(command.getName());
        if (pluginCommand == null) throw new NullPointerException("Bukkit PluginCommand is null!");

        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
    }
}
