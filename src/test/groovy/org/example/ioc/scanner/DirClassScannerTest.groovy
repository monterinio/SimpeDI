package org.example.ioc.scanner

import spock.lang.Specification

import java.nio.file.Path

class DirClassScannerTest extends Specification {

    private DirClassScanner cut = new DirClassScanner()

    def "should locate classes directly"() {
        given:
        def root = System.getProperty("user.dir")
        def folders = "build/classes/java/main/"
        def filePath = Path.of(root, folders)

        when:
        def result = cut.locate(filePath.toString())


        then:
        verifyAll {
            result != null
            result.size() > 0
            result.contains(ClassScanner.class)
        }
    }
}
