package org.example.ioc.directory;

class ClassScannerFactory {

    private ClassScannerFactory() {
    }

    static ClassScanner getClassScanner(DirectoryType directoryType) {
        if (directoryType == DirectoryType.DIRECTORY) {
            return new DirClassScanner();
        }

        if (directoryType == DirectoryType.JAR_FILE) {
            return new JarClassScanner();
        }

        throw new IllegalStateException("Unable to construct ClassScanner instance for directoryType: " + directoryType);
    }
}