package org.example.ioc.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ServiceModel<T> implements Comparable<ServiceModel<?>> {

  public final Class<T> ownType;
  public final Annotation annotation;
  public final Constructor<T> constructor;
  public final Method postConstruct;
  public final Method preDestroy;
  public final Method[] beans;
  private final List<ServiceModel<?>> dependants = new ArrayList<>();
  public Object instance;

  private ServiceModel(Class<T> ownType, Annotation annotation, Constructor<T> constructor,
      Method postConstruct,
      Method preDestroy, Method[] beans) {
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

  static class ServiceBuilder<T> {

    public Class<T> ownType;
    public Annotation annotation;
    public Constructor<T> constructor;
    public Method postConstruct;
    public Method preDestroy;
    public Method[] beans;

    public ServiceModel<T> create() {
      return new ServiceModel<>(ownType, annotation, constructor, postConstruct, preDestroy, beans);
    }
  }
}