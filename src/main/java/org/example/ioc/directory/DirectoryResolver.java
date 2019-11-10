package org.example.ioc.directory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.example.ioc.directory.DirectoryType.*;

class DirectoryResolver {

    private static final String JAR_MIME = "application/java-archive";
    private Logger logger = LoggerFactory.getLogger(DirectoryResolver.class);

    Directory resolve(Class<?> entry) {
        var dir = entry.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile();
        var dirType = getDirType(dir);

        return new Directory(dir, dirType);
    }

    private DirectoryType getDirType(String dir) {
        var file = new File(dir);

        if (file.isDirectory()) {
            logger.debug("File {} is a directory.", file);
            return DIRECTORY;
        }

        try {
            var contentType = Files.probeContentType(Path.of(dir));
            logger.debug("File {} is of content type {}", file, contentType);

            return JAR_MIME.equals(contentType) ?
                    JAR_FILE :
                    UNKNOWN;
        } catch (IOException e) {
            throw new IllegalStateException("Could not determine type of a directory: " + file, e);
        }
    }
}
