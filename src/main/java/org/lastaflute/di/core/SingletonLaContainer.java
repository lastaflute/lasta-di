/*
 * Copyright 2015-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.lastaflute.di.core;

import org.lastaflute.di.core.exception.ComponentNotFoundException;
import org.lastaflute.di.core.exception.CyclicReferenceComponentException;
import org.lastaflute.di.core.exception.TooManyRegistrationComponentException;
import org.lastaflute.di.core.factory.SingletonLaContainerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class SingletonLaContainer {

    private SingletonLaContainer() {
    }

    /**
     * @param componentType The component type to find. (NotNull)
     * @return The found component. (NotNull)
     * @throws ComponentNotFoundException When the component is not found by the type.
     * @throws TooManyRegistrationComponentException When the component key is related to plural components.
     * @throws CyclicReferenceComponentException When the components refers each other.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getComponent(Class<T> componentType) {
        return (T) SingletonLaContainerFactory.getContainer().getComponent(componentType);
    }

    /**
     * @param componentName The component name to find. (NotNull)
     * @return The found component. (NotNull)
     * @throws ComponentNotFoundException When the component is not found by the type.
     * @throws TooManyRegistrationComponentException When the component key is related to plural components.
     * @throws CyclicReferenceComponentException When the components refers each other.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getComponent(String componentName) {
        return (T) SingletonLaContainerFactory.getContainer().getComponent(componentName);
    }
}
