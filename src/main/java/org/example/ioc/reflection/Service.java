package org.example.ioc.reflection;

import org.example.ioc.annotation.Inject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

class Service {

    public static final String MISSING_FALLBACK_METHOD_EXCEPTION_MSG = "Unable to identify default method: aVoid in Service.class";
    private static final String NO_CONSTRUCTOR_ANNOTATION_EXCEPTION_MSG = "Unable to instantiate class %s. Multiple constructors were found. " +
            "Please mark explicitly, f.e. with @Inject, which constructor should be used for construction.";
    private static final String MULTIPLE_CONSTRUCTOR_ANNOTATIONS_EXCEPTION_MSG = "Unable to instantiate class %s. Multiple constructors were marked" +
            " with construction annotation (which is @Inject by default). Please remove ambiguity.";
    private static final String MULTIPLE_HOOK_ANNOTATIONS_EXCEPTION_MSG = "Unable to instantiate class %s. Multiple methods were marked" +
            " with the same hook annotation %s (which is @PostConstruct/@PreDestroy by default). Please remove ambiguity.";
    private static final String WRONG_HOOK_RETURN_TYPE_EXCEPTION_MSG = "Invalid return type for method annotated with %s withing class %s. Expected void or Void, got %s";
    private Class<?> clazz;

    public Service(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Method getMethod(Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(annotation) != null)
                .collect(collectingAndThen(
                        toList(),
                        list -> this.validateResult(list, annotation)
                ));
    }

    private Method validateResult(List<Method> methods, Class<? extends Annotation> annotation) {
        if (methods.isEmpty()) {
            try {
                return Service.class.getDeclaredMethod("aVoid");
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(MISSING_FALLBACK_METHOD_EXCEPTION_MSG, e);
            }
        }

        if (methods.size() > 1) {
            throw new IllegalStateException(format(MULTIPLE_HOOK_ANNOTATIONS_EXCEPTION_MSG, clazz, annotation));
        }

        var method = methods.get(0);
        if (method.getReturnType() == void.class) {
            return method;
        } else {
            throw new IllegalStateException(format(WRONG_HOOK_RETURN_TYPE_EXCEPTION_MSG, annotation, clazz, method.getReturnType()));
        }
    }

    @SuppressWarnings("unused")
    private void aVoid() {
        // fallback method
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
