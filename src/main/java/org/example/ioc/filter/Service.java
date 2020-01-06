package org.example.ioc.filter;

import org.example.ioc.annotation.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

class Service {

    static final String NO_CONSTRUCTOR_ANNOTATION_EXCEPTION_MSG = "Unable to instantiate class %s. Multiple constructors were found. " +
            "Please mark explicitly, f.e. with @Inject, which constructor should be used for construction.";
    static final String MULTIPLE_CONSTRUCTOR_ANNOTATIONS_EXCEPTION_MSG = "Unable to instantiate class %s. Multiple constructors were marked" +
            " with construction annotation (which is @Inject by default). Please remove ambiguity.";
    static final String MULTIPLE_HOOK_ANNOTATIONS_EXCEPTION_MSG = "Unable to instantiate class %s. Multiple methods were marked" +
            " with the same hook annotation %s (which is @PostConstruct/@PreDestroy by default). Please remove ambiguity.";
    static final String WRONG_HOOK_RETURN_TYPE_EXCEPTION_MSG = "Invalid return type for method annotated with %s withing class %s. Expected void or Void, got %s";
    public final Class<?> clazz;

    public Service(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Optional<Method> getMethod(Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(annotation) != null)
                .collect(collectingAndThen(
                        toList(),
                        list -> this.validateResult(list, annotation)
                ));
    }

    public Constructor getValidConstructor() {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        if (constructors.length == 1) {
            return constructors[0];
        }

        return Arrays.stream(constructors)
                .filter(constr -> constr.getAnnotation(Inject.class) != null)
                .collect(collectingAndThen(
                        toList(),
                        this::validateResult)
                );
    }

    private Optional<Method> validateResult(List<Method> methods, Class<? extends Annotation> annotation) {
        if (methods.isEmpty()) {
            return Optional.empty();
        }

        if (methods.size() > 1) {
            throw new IllegalStateException(format(MULTIPLE_HOOK_ANNOTATIONS_EXCEPTION_MSG, clazz, annotation));
        }

        var method = methods.get(0);
        if (method.getReturnType() == void.class) {
            return Optional.of(method);
        } else {
            throw new IllegalStateException(format(WRONG_HOOK_RETURN_TYPE_EXCEPTION_MSG, annotation, clazz, method.getReturnType()));
        }
    }

    private Constructor<?> validateResult(List<Constructor<?>> constructors) {
        if (constructors.isEmpty()) {
            throw new IllegalStateException(format(NO_CONSTRUCTOR_ANNOTATION_EXCEPTION_MSG, clazz));
        }

        if (constructors.size() > 1) {
            throw new IllegalStateException(format(MULTIPLE_CONSTRUCTOR_ANNOTATIONS_EXCEPTION_MSG, clazz));
        }

        return constructors.get(0);
    }

}
