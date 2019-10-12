import spock.lang.Specification

class Test extends Specification {

    def "should test"() {
        given:
        def a = "test"

        when: "a is concatenated"
        def result = a + "123"

        then:
        result == "test123"
    }

}
