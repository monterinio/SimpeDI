package org.example.ioc.filter


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

            def postConstruct = result[0].postConstruct
            postConstruct != null
            result[0].postConstruct.isPresent()

            def postConstructUnboxed = result[0].postConstruct.get()
            postConstructUnboxed.toString() == "public void org.example.ioc.filter.TestOne.test()"
            result[0].constructor.toString() == "org.example.ioc.filter.TestOne(org.example.ioc.filter.TestTwo)"
        }
    }

}
