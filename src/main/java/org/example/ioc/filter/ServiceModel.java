package org.example.ioc.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ServiceModel<T> implements Comparable<ServiceModel<?>> {

    static final String NOT_MATCHING_ARGUMENT_COUNT_MSG = "Constructor parameter count: %d is not equal to provided constructor parameter number: %d.";

    public final Class<T> ownType;
    public final Annotation annotation;
    public final Constructor<T> constructor;
    public final Optional<Method> postConstruct;
    public final Optional<Method> preDestroy;
    public final Method[] beans;
    public final List<ServiceModel<?>> dependants = new ArrayList<>();
    public Object instance;

    private ServiceModel(Class<T> ownType, Annotation annotation, Constructor<T> constructor,
                         Optional<Method> postConstruct,
                         Optional<Method> preDestroy, Method[] beans) {
        this.ownType = ownType;
        this.annotation = annotation;
        this.constructor = constructor;
        this.postConstruct = postConstruct;
        this.preDestroy = preDestroy;
        this.beans = beans;
    }

    public void eraseInstance() {
        instance = null;
    }

    public Object createInstance(Object... constructorParams) {

        if (constructor.getParameterCount() != constructorParams.length) {
            throw new IllegalStateException(String.format(NOT_MATCHING_ARGUMENT_COUNT_MSG,
                    constructor.getParameterCount(), constructorParams.length));
        }

        try {
            var instance = constructor.newInstance(constructorParams);
            postConstruct.ifPresent(method -> invokeOrThrowRuntime(instance, method));

            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Error while creating an instance.", e);
        }
    }

    void destroyInstance(ServiceModel<?> serviceModel) {
        var preDestroyMethod = serviceModel.preDestroy;

        preDestroyMethod.ifPresent(method -> invokeOrThrowRuntime(serviceModel.instance, method));
        serviceModel.eraseInstance();

    }

    @Override
    public int compareTo(ServiceModel<?> that) {
        return Integer
                .compare(this.constructor.getParameterCount(), that.constructor.getParameterCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
        int result = Objects.hash(ownType, annotation, constructor, instance, postConstruct, preDestroy,
                dependants);
        result = 31 * result + Arrays.hashCode(beans);
        return result;
    }

    private void invokeOrThrowRuntime(Object instance, Method method) {
        try {
            method.invoke(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    static class ServiceBuilder<T> {

        public Class<T> ownType;
        public Annotation annotation;
        public Constructor<T> constructor;
        public Optional<Method> postConstruct;
        public Optional<Method> preDestroy;
        public Method[] beans;

        public ServiceModel<T> create() {
            return new ServiceModel<>(ownType, annotation, constructor, postConstruct, preDestroy, beans);
        }
    }
}