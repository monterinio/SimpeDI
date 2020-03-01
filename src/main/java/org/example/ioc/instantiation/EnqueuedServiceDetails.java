package org.example.ioc.instantiation;

import org.example.ioc.filter.ServiceModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class EnqueuedServiceDetails {

    private final ServiceModel<?> serviceModel;
    private final Class<?>[] dependencies;
    private final Map<Class, Object> instancesMap;

    EnqueuedServiceDetails(ServiceModel<?> serviceModel) {
        this.serviceModel = serviceModel;
        this.dependencies = serviceModel.constructor.getParameterTypes();
        this.instancesMap = new HashMap<>();
        for (Class<?> parameterType : serviceModel.constructor.getParameterTypes()) {
            instancesMap.put(parameterType, null);
        }
    }

    boolean areDependenciesInstantiated() {
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

    ServiceModel<?> getServiceModel() {
        return serviceModel;
    }

    Object[] getDependencies() {
        return instancesMap
                .values()
                .toArray();
    }
}
