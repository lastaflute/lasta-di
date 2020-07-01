/*
 * Copyright 2015-2020 the original author or authors.
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
package org.lastaflute.di.redefiner;

import org.lastaflute.di.core.LaContainer;
import org.lastaflute.di.core.factory.LaContainerFactory;

/**
 * @author modified by jflute (originated in Ymir)
 */
public class LaContainerPreparer {

    protected LaContainer container;

    public void include() {
    }

    @SuppressWarnings("unchecked")
    public final <T> T getComponent(Class<T> key) {
        return (T) container.getComponent(key);
    }

    public final Object getComponent(Object key) {
        return container.getComponent(key);
    }

    public final void include(Class<? extends LaContainerPreparer> preparer) {
        include(preparer.getName().replace('.', '/').concat(".class"));
    }

    public final void include(String path) {
        LaContainerFactory.include(container, path);
    }

    public final LaContainer getContainer() {
        return container;
    }

    public final void setContainer(LaContainer container) {
        this.container = container;
    }
}
