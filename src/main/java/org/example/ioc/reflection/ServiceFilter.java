package org.example.ioc.reflection;

import org.example.ioc.annotation.Component;

import java.util.Objects;
import java.util.Set;

import static java.util.function.Predicate.not;

public class ServiceFilter {

    public Set<ServiceModel> findServices(Set<Class<?>> locatedClasses) {
        locatedClasses.stream()
                .filter(not(Class::isAnnotation))
                .filter(not(Class::isInterface))
                .filter(not(Class::isEnum))
                .filter(not(Class::isLocalClass))
                .filter(this::isComponent)
                .map(Service::new);
//                .map(cls -> new ServiceBuilder() {{
//                    ownType = cls;
//                    annotation = cls.getAnnotation(Component.class);
//                    constructor = getValidConstructor(cls);
//                    postConstruct = getMethod(cls, PostConstruct.class);
//                }}.create());

        return null;
    }

    private boolean isComponent(Class<?> clazz) {
        return Objects.nonNull(clazz.getAnnotation(Component.class));
    }


}
