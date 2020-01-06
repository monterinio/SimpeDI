package org.example.ioc.scanner;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class JarClassScanner implements ClassScanner {

    private static final String CLASS_SUFFIX = ".class";

    @Override
    public Set<Class<?>> locate(String dir) {

        try (JarFile jarFile = new JarFile(new File(dir))) {
            var entries = jarFile.entries();
            Iterable<JarEntry> iterable = entries::asIterator;

            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(JarEntry::getName)
                    .filter(this::hasClassExtension)
                    .map(this::toFullPackageName)
                    .map(this::toClass)
                    .collect(Collectors.toSet());

        } catch (IOException e) {
            throw new IllegalStateException("Could not create JarFile for directory: " + dir, e);
        }
    }

    private String toFullPackageName(String str) {
        return str.replace(CLASS_SUFFIX, "")
                .replaceAll("\"", ".")
                .replace("/", ".");
    }
}
