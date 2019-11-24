package org.example.ioc.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

class ServiceModel<T> implements Comparable<ServiceModel<?>> {

    private final List<ServiceModel<?>> dependants = new ArrayList<>();
    private Class<T> ownType;
    private Annotation annotation;
    private Constructor<T> constructor;
    private T instance;
    private Method postConstruct;
    private Method preDestroy;
    private Method[] beans;

    private ServiceModel(Class<T> ownType, Annotation annotation, Constructor<T> constructor, Method postConstruct, Method preDestroy) {
        this.ownType = ownType;
        this.annotation = annotation;
        this.constructor = constructor;
        this.postConstruct = postConstruct;
        this.preDestroy = preDestroy;
    }

    @Override
    public int compareTo(ServiceModel<?> that) {
        return Integer.compare(this.constructor.getParameterCount(), that.constructor.getParameterCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceModel<?> that = (ServiceModel<?>) o;
        return ownType.equals(that.ownType) &&
                annotation.equals(that.annotation) &&
                constructor.equals(that.constructor) &&
                instance.equals(that.instance) &&
                Objects.equals(postConstruct, that.postConstruct) &&
                Objects.equals(preDestroy, that.preDestroy) &&
                Arrays.equals(beans, that.beans) &&
                Objects.equals(dependants, that.dependants);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(ownType, annotation, constructor, instance, postConstruct, preDestroy, dependants);
        result = 31 * result + Arrays.hashCode(beans);
        return result;
    }

    static class ServiceBuilder<T> {
        public Class<T> ownType;
        public Annotation annotation;
        public Constructor<T> constructor;
        public Method postConstruct;
        public Method preDestroy;
        public Method[] beans;

        public ServiceModel<T> create() {
            return new ServiceModel<T>(ownType, annotation, constructor, postConstruct, preDestroy);
        }
    }
}