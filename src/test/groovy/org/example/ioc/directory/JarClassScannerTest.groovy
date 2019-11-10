package org.example.ioc.directory

import spock.lang.Specification

class JarClassScannerTest extends Specification {

    private ClassScanner cut = new JarClassScanner()

    def "should locate classes inside jar file"() {
        given: "existing jar file"
        def input = getClass()
                        .getClassLoader()
                        .getResource("test.jar")
                        .getPath()

        when:
        def result = cut.locate(input)

        then:
        result != []
    }

}
