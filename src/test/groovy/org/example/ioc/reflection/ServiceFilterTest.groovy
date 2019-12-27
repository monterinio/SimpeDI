package org.example.ioc.reflection


import spock.lang.Specification

class ServiceFilterTest extends Specification {

    def "should filter annotated Services and order by least amount of dependencies"() {
        given:
        def inputClasses = new HashSet<>([TestOne.class, TestTwo.class, TestThree.class])

        when:
        Set<ServiceModel> result = ServiceFilter.findServices(inputClasses)

        then:
        verifyAll {
            result.size() == 2
            result[0].ownType == TestTwo.class
            result[1].ownType == TestOne.class
        }
    }

    def "should read constructor and postConstruct method"() {
        given:
        def inputClasses = new HashSet<>([TestOne.class])

        when:
        Set<ServiceModel> result = ServiceFilter.findServices(inputClasses)

        then:
        verifyAll {
            result.size() == 1
            result[0].ownType == TestOne.class
            result[0].postConstruct.toString() == "public void org.example.ioc.reflection.TestOne.test()"
            result[0].constructor.toString() == "org.example.ioc.reflection.TestOne(org.example.ioc.reflection.TestTwo)"
        }
    }

}
