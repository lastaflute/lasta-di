/*
 * Copyright 2014-2015 the original author or authors.
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
package org.dbflute.lasta.di.core;

import org.dbflute.lasta.di.core.factory.SingletonLaContainerFactory;

/**
 * @author modified by jflute (originated in Seasar)
 */
public abstract class SingletonLaContainer {

    private SingletonLaContainer() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T getComponent(Class<T> componentClass) {
        return (T) SingletonLaContainerFactory.getContainer().getComponent(componentClass);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getComponent(String componentName) {
        return (T) SingletonLaContainerFactory.getContainer().getComponent(componentName);
    }

    @SuppressWarnings("unchecked")
    public static <COMPONENT> COMPONENT[] findAllComponents(Class<COMPONENT> type) {
        return (COMPONENT[]) SingletonLaContainerFactory.getContainer().findAllComponents(type);
    }
}
