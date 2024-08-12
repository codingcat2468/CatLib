package com.codingcat.catlib.papi.expansions;

import com.codingcat.catlib.basic.PluginDependentClass;
import com.codingcat.catlib.util.ReflectUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

public class PAPIExpansionsLoader extends PluginDependentClass {
    private final static String PLUGIN_EXPANSIONS_PACKAGE = ".papi.expansions";
    private final String packageName;
    public PAPIExpansionsLoader(String packageName, JavaPlugin plugin) {
        super(plugin);
        this.packageName = packageName;
    }

    public void initAll(Class<? extends JavaPlugin> pluginClass) {
        String packageName = this.packageName + PAPIExpansionsLoader.PLUGIN_EXPANSIONS_PACKAGE;
        Set<? extends Class<? extends PlaceholderExpansion>> expansionClasses = ReflectUtil.getClassesImplementingOrExtending(packageName, PlaceholderExpansion.class);

        for (Class<? extends PlaceholderExpansion> expansionClass : expansionClasses) {
            try {
                PlaceholderExpansion expansion;
                Constructor<?>[] constructors = expansionClass.getConstructors();
                boolean hasPluginConstructor = !Arrays.stream(constructors).filter(constructor ->
                        (constructor.getParameterCount() == 1) && (constructor.getParameters()[0].getType() == pluginClass)).toList().isEmpty();

                if (hasPluginConstructor) {
                    Constructor<? extends PlaceholderExpansion> pluginDependentConstructor = expansionClass.getConstructor(pluginClass);
                    expansion = pluginDependentConstructor.newInstance(getPlugin());
                } else {
                    Constructor<? extends PlaceholderExpansion> constructor = expansionClass.getConstructor();
                    expansion = constructor.newInstance();
                }

                expansion.register();
            } catch (ReflectiveOperationException exception) {
                getLogger().warning(String.format("Cannot register PAPI expansion class %s: %s", expansionClass.getName(), exception));
            }
        }
    }
}
