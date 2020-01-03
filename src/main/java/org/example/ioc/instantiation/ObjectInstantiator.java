package org.example.ioc.instantiation;

import java.lang.reflect.InvocationTargetException;
import org.example.ioc.reflection.ServiceModel;

class ObjectInstantiator {

  void createInstance(ServiceModel<?> serviceModel, Object... constructorParams) {
    var targetConstructor = serviceModel.constructor;

    if (targetConstructor.getParameterCount() != constructorParams.length) {
      // throw
    }

    try {
      var instance = serviceModel.constructor.newInstance(constructorParams);
      serviceModel.instance = instance;

      var postConstructMethod = serviceModel.postConstruct;
      postConstructMethod.invoke(instance);

    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  void destroyInstance(ServiceModel<?> serviceModel) {
    var preDestroyMethod = serviceModel.preDestroy;

    try {
      preDestroyMethod.invoke(serviceModel.instance);
      serviceModel.eraseInstance();

    } catch (IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }
}
