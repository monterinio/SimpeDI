package org.example.ioc.filter;

import org.example.ioc.annotation.Component;
import org.example.ioc.annotation.PostConstruct;
import org.example.ioc.annotation.PreDestroy;
import org.example.ioc.filter.ServiceModel.ServiceBuilder;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class ServiceFilter {

    private ServiceFilter() {
    }

    public static Set<ServiceModel> findServices(Set<Class<?>> locatedClasses) {
        return locatedClasses.stream()
                .filter(not(Class::isAnnotation))
                .filter(not(Class::isInterface))
                .filter(not(Class::isEnum))
                .filter(not(Class::isLocalClass))
                .filter(ServiceFilter::isComponent)
                .map(Service::new)
                .map(service -> new ServiceBuilder() {{
                    ownType = service.clazz;
                    annotation = service.clazz.getAnnotation(Component.class);
                    constructor = service.getValidConstructor();
                    postConstruct = service.getMethod(PostConstruct.class);
                    preDestroy = service.getMethod(PreDestroy.class);
                }}.create())
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static boolean isComponent(Class<?> clazz) {
        return Objects.nonNull(clazz.getAnnotation(Component.class));
    }
}
