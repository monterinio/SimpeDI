package org.example.ioc.instantiation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.example.ioc.reflection.ServiceModel;

public class ServiceInstantiator {

  private final ObjectInstantiator instantiator;
  private final LinkedList<EnquedServiceDetails> enqueuedServiceDetails;
  private final List<Class<?>> availableClasses;

  public ServiceInstantiator(ObjectInstantiator instantiator) {
    this.instantiator = instantiator;
    this.availableClasses = new ArrayList<>();
    this.enqueuedServiceDetails = new LinkedList<>();
  }

  List<ServiceModel<?>> instantiate(Set<ServiceModel<?>> serviceModels) {

    serviceModels.forEach(model -> {
      enqueuedServiceDetails.add(new EnquedServiceDetails(model));
      availableClasses.add(model.ownType);
    });

    int counter = 0;
    while(this.enqueuedServiceDetails.isEmpty()) {
      // todo-major: extract value to config class
      if (counter > 100_000) {
        // todo-minor: log and rethrow
      }

      // todo-major: 41.27 finish implementation
      var serviceDetail = enqueuedServiceDetails.removeFirst();
      if (serviceDetail.areInstantiated()) {
//        instantiator.createInstance(serviceDetail.);
      } else {
        counter++;
      }
    }

    return null;
  }
}
