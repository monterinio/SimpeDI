package org.example.ioc.directory

import spock.lang.Specification

import java.nio.file.Path

class DirClassScannerTest extends Specification {

    private DirClassScanner cut = new DirClassScanner()

    def "should locate classes directly"() {
        given:
        def root = System.getProperty("user.dir")
        def folders = "out/production/classes"
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
