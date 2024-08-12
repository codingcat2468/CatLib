package com.codingcat.catlib.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.Set;
import java.util.stream.Collectors;

public class ReflectUtil {
    public static <T> Set<? extends Class<? extends T>> getClassesImplementingOrExtending(String packageName, Class<? extends T> clazz) {
        try (ScanResult result = new ClassGraph()
                .enableAllInfo()
                .acceptPackages(packageName)
                .scan()) {

            return result.getAllClasses()
                    .stream().filter(classInfo -> !classInfo.getSuperclasses().stream().filter(
                            superclass -> superclass.loadClass() == clazz
                    ).toList().isEmpty() || (clazz.isInterface() && classInfo.implementsInterface(clazz)))
                    .map(classInfo -> classInfo.loadClass(clazz))
                    .collect(Collectors.toSet());
        }
    }
}
