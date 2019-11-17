package org.example.ioc.directory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

class DirClassScanner implements ClassScanner {

    private Set<Class<?>> classes = new HashSet<>();
    private static final String CLASS_SUFFIX = ".class";
    private static final String INTELLIJ_OUTPUT_FOLDER = "classes.";
    private static final String GRADLE_OUTPUT_FOLDER = "main.";
    @Override
    public Set<Class<?>> locate(String dir) {
        var file = new File(dir);

        if (!file.isDirectory()) {
            throw new IllegalStateException("File " + dir + " is not a directory!");
        }
        scanDir(file, "");
        return classes;
    }

    private void scanDir(File file, String packageName) {

        final String currentPackage;

        if (file.isDirectory()) {
            currentPackage = packageName + file.getName() + ".";
            for (File thisFile : file.listFiles()) {
                scanDir(thisFile, currentPackage);
            }
        } else if (hasClassExtension(file.getName())) {
            var className = toFullPackageName(packageName, file.getName());
            var clazz = toClass(className);
            classes.add(clazz);
        }
    }

    private String toFullPackageName(String packageName, String fileName) {
        return packageName
                .replaceFirst(INTELLIJ_OUTPUT_FOLDER, "")
                .replaceFirst(GRADLE_OUTPUT_FOLDER, "")
                + fileName.replaceFirst(CLASS_SUFFIX, "");
    }
}
