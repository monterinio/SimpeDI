package org.example.ioc.directory;

import org.example.ioc.config.Configuration;
import org.example.ioc.reflection.ServiceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

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
        logger.info("{}", classScanner.locate(dir.getDir()));

        logger.info("{}", (ServiceFilter.findServices(classScanner.locate(dir.getDir()))));

    }
}
