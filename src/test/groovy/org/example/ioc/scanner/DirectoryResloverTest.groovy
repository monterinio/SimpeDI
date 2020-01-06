package org.example.ioc.scanner

import spock.lang.Specification

import java.nio.file.Files

class DirectoryResloverTest extends Specification {

    def cut = new DirectoryResolver()

    def "should get directory of type JAR_FILE"() {
        given: "path to a jar file"
        def path = getClass()
                    .getClassLoader()
                    .getResource("test.jar")
                    .getPath()

        when:
        def result = cut.getDirType(path)

        then:
        result == DirectoryType.JAR_FILE
    }

    def "should get directory of type DIRECTORY"() {
        given: "path to a directory"
        def path = Files.createTempDirectory("temp")
                    .toString()

        when:
        def result = cut.getDirType(path)

        then:
        result == DirectoryType.DIRECTORY
    }
}
