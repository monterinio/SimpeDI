package org.example.ioc.filter

import org.example.ioc.annotation.PostConstruct
import spock.lang.Specification

import static java.lang.String.format

class ServiceTest extends Specification {

    def "should get automatically generated no-arg constructor"() {
        given:
        Class<AutoGeneratedNoArgConstructorTest> inputClass = AutoGeneratedNoArgConstructorTest.class
        Service cut = new Service(inputClass)

        when:
        def constructor = cut.getValidConstructor()

        then:
        constructor != null
        constructor.name == "org.example.ioc.filter.AutoGeneratedNoArgConstructorTest"
    }

    def "should get self generated no-arg constructor"() {
        given:
        Class<ExplicitlyGeneratedNoArgConstructorTest> inputClass = ExplicitlyGeneratedNoArgConstructorTest.class
        Service cut = new Service(inputClass)

        when:
        def constructor = cut.getValidConstructor()

        then:
        constructor != null
        constructor.name == "org.example.ioc.filter.ExplicitlyGeneratedNoArgConstructorTest"
        constructor.parameterCount == 1
    }

    def "should get marked constructor"() {
        given:
        Class<ManyConstructorsWithAnnotationTest> inputClass = ManyConstructorsWithAnnotationTest.class
        Service cut = new Service(inputClass)

        when:
        def constructor = cut.getValidConstructor()

        then:
        constructor != null
        constructor.name == "org.example.ioc.filter.ManyConstructorsWithAnnotationTest"
        constructor.parameterCount == 2
    }

    def "should throw exception when no constructor is marked"() {
        given:
        Class<ManyConstructorsWithoutAnnotationTest> inputClass = ManyConstructorsWithoutAnnotationTest.class
        Service cut = new Service(inputClass)

        when:
        cut.getValidConstructor()

        then:
        def exception = thrown IllegalStateException
        exception.message == format(cut.NO_CONSTRUCTOR_ANNOTATION_EXCEPTION_MSG, inputClass)
    }

    def "should throw exception when multiple constructors are marked"() {
        given:
        Class<MultiplyAnnotatedConstructorsTest> inputClass = MultiplyAnnotatedConstructorsTest.class
        Service cut = new Service(inputClass)

        when:
        cut.getValidConstructor()

        then:
        def exception = thrown IllegalStateException
        exception.message == format(cut.MULTIPLE_CONSTRUCTOR_ANNOTATIONS_EXCEPTION_MSG, inputClass)
    }

    def "should get post-construct method with primitive void return type"() {
        given:
        Class<PostConstructMethodTest> inputClass = PostConstructMethodTest
        Service cut = new Service(inputClass)

        when:
        def result = cut.getMethod(PostConstruct)

        then:
        verifyAll {
            result != null
            result.isPresent() == true

            def unboxedResult = result.get()
            unboxedResult.name == "postConstructInit"
            unboxedResult.getReturnType() == void.class
            unboxedResult.getParameterCount() == 0
        }
    }

    def "should get default method if there isn't any explicitly marked"() {
        given:
        Class<NoPostConstructMethodTest> inputClass = NoPostConstructMethodTest
        Service cut = new Service(inputClass)

        when:
        def result = cut.getMethod(PostConstruct)

        then:
        verifyAll {
            result != null
            result.isPresent() == false
        }
    }

    def "should throw exception if post-construct has invalid return type"() {
        given:
        Class<PostConstructInvalidReturnMethodTest> inputClass = PostConstructInvalidReturnMethodTest
        Service cut = new Service(inputClass)

        when:
        cut.getMethod(PostConstruct)

        then:
        def exception = thrown IllegalStateException
        exception.message == format(cut.WRONG_HOOK_RETURN_TYPE_EXCEPTION_MSG, PostConstruct, PostConstructInvalidReturnMethodTest, int)
    }

    def "should throw exception if there are multiple post-construct methods"() {
        given:
        Class<MultiplePostConstructMethodTest> inputClass = MultiplePostConstructMethodTest
        Service cut = new Service(inputClass)

        when:
        cut.getMethod(PostConstruct)

        then:
        def exception = thrown IllegalStateException
        exception.message == format(cut.MULTIPLE_HOOK_ANNOTATIONS_EXCEPTION_MSG, MultiplePostConstructMethodTest, PostConstruct)
    }
}
