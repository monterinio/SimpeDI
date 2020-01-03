package org.example.ioc.instantiation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.example.ioc.reflection.ServiceModel;

class EnquedServiceDetails {

  private final ServiceModel<?> serviceModel;
  private final Class<?>[] dependencies;
  private final Map<Class, Object> instancesMap;

  EnquedServiceDetails(ServiceModel<?> serviceModel) {
    this.serviceModel = serviceModel;
    this.dependencies = serviceModel.constructor.getParameterTypes();
    this.instancesMap = new HashMap<>();
  }

  boolean areInstantiated() {
    return instancesMap.values()
        .stream()
        .noneMatch(x -> x == null);
  }

  boolean isDependencyRequired(Class<?> dependencyType) {
    return Arrays.stream(dependencies)
        .anyMatch(dependency -> dependency.isAssignableFrom(dependencyType));
  }

  void addDependencyInstance(Object instance) {
    Arrays.stream(dependencies)
        .filter(dependency -> dependency.isAssignableFrom(instance.getClass()))
        .forEach(dependency -> instancesMap.put(dependency, instance));
  }
}
