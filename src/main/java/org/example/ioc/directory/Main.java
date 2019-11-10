package org.example.ioc.directory;

import org.example.ioc.config.Configuration;

public class Main {

    public static void main(String[] args) {
        run(Main.class);
    }

    private static void run(Class<?> entry) {
        run(entry, new Configuration());
    }

    private static void run(Class<?> entry, Configuration configuration) {
        var dir = new DirectoryResolver()
                        .resolve(entry);
        ClassScanner classScanner = ClassScannerFactory.getClassScanner(dir.getDirType());
        System.out.println(classScanner.locate(dir.getDir()));
    }
}
