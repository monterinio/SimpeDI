package org.example.ioc.scanner;

import java.util.Set;

public interface ClassScanner {

    Set<Class<?>> locate(String dir);

    default boolean hasClassExtension(String entryName) {
        return entryName.endsWith(".class");
    }

    default Class<?> toClass(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class " + str + " could not be instantiated.", e);
        }
    }
}