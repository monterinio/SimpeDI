package org.example.ioc.instantiation;

import org.example.ioc.filter.ServiceModel;

import java.util.*;

public class ServiceInstantiator {

    final static String MAX_NESTING_FACTOR_EXCEEDED = "Exceeded max. nesting factor (100 000). Unable to instantiate a bean.";

    private final static int MAX_NESTING_FACTOR = 100_000;
    public static final String COMPONENT_NOT_EXISTING_MSG = "Component for class %s was not created. Is your class marked with @Component or @Bean annotation?";
    private final LinkedList<EnqueuedServiceDetails> enqueuedServiceDetails;
    private final List<Class<?>> availableClasses;
    private final List<ServiceModel> instantiatedServices;

    public ServiceInstantiator() {
        this.availableClasses = new ArrayList<>();
        this.enqueuedServiceDetails = new LinkedList<>();
        this.instantiatedServices = new ArrayList<>();
    }

    public List<ServiceModel> instantiate(Set<ServiceModel> serviceModels) {

        serviceModels.forEach(model -> {
            enqueuedServiceDetails.add(new EnqueuedServiceDetails(model));
            availableClasses.add(model.ownType);
        });

        validateMissingDependencies(serviceModels);

        int counter = 0;
        while (!this.enqueuedServiceDetails.isEmpty()) {
            if (counter > MAX_NESTING_FACTOR) {
                throw new IllegalStateException(MAX_NESTING_FACTOR_EXCEEDED);
            }

            var poppedServiceDetails = enqueuedServiceDetails.removeFirst();
            if (poppedServiceDetails.areDependenciesInstantiated()) {
                final ServiceModel<?> serviceModel = poppedServiceDetails.getServiceModel();
                var dependencies = poppedServiceDetails.getDependencies();

                serviceModel.createInstance(dependencies);
                registerService(serviceModel);

            } else {
                enqueuedServiceDetails.addLast(poppedServiceDetails);
                counter++;
            }
        }

        return instantiatedServices;
    }

    private void validateMissingDependencies(Set<ServiceModel> serviceModels) {
        serviceModels.stream()
                .flatMap(serviceModel -> Arrays.stream(serviceModel.constructor.getParameterTypes()))
                .distinct()
                .forEach(parameterType -> {
                    if (!isAssignableTypePresent(parameterType)) {
                        throw new IllegalStateException(COMPONENT_NOT_EXISTING_MSG);
                    }
                });
//        for (ServiceModel<?> serviceModel : serviceModels) {
//            for (Class<?> parameterType : serviceModel.constructor.getParameterTypes()) {
//                if (!isAssignableTypePresent(parameterType)) {
//                    // todo:
//                    throw new IllegalStateException();
//                }
//            }
//        }
    }

    private boolean isAssignableTypePresent(Class<?> clazz) {
        return availableClasses.stream()
                .anyMatch(cls -> cls.isAssignableFrom(clazz));
    }

    private void registerService(ServiceModel<?> serviceModel) {
        instantiatedServices.add(serviceModel);

        updateDependantServices(serviceModel);

        enqueuedServiceDetails.stream()
                .filter(detail -> detail.isDependencyRequired(serviceModel.ownType))
                .forEach(detail -> detail.addDependencyInstance(serviceModel.instance));
    }

    private void updateDependantServices(ServiceModel serviceModel) {
        for (Class parameterType : serviceModel.constructor.getParameterTypes()) {
            for (ServiceModel instantiatedService : instantiatedServices) {
                if (parameterType.isAssignableFrom(instantiatedService.ownType)) {
                    instantiatedService.dependants.add(serviceModel);
                }
            }
        }
    }
}
